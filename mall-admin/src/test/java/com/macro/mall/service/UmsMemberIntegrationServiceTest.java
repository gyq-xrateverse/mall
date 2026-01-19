package com.macro.mall.service;

import com.macro.mall.common.service.RedisService;
import com.macro.mall.dao.UmsMemberDao;
import com.macro.mall.dto.UmsMemberIntegrationParam;
import com.macro.mall.dto.UmsMemberIntegrationQuery;
import com.macro.mall.dto.UmsMemberIntegrationVO;
import com.macro.mall.mapper.UmsIntegrationChangeHistoryMapper;
import com.macro.mall.mapper.UmsIntegrationFreezeMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.model.UmsIntegrationFreeze;
import com.macro.mall.model.UmsMember;
import com.macro.mall.service.impl.UmsMemberIntegrationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UmsMemberIntegrationService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class UmsMemberIntegrationServiceTest {

    @Mock
    private UmsMemberMapper memberMapper;

    @Mock
    private UmsIntegrationFreezeMapper freezeMapper;

    @Mock
    private UmsIntegrationChangeHistoryMapper historyMapper;

    @Mock
    private UmsMemberDao memberDao;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private UmsMemberIntegrationServiceImpl integrationService;

    private UmsMember testMember;
    private UmsMemberIntegrationParam testParam;

    @BeforeEach
    public void setUp() {
        testMember = new UmsMember();
        testMember.setId(1L);
        testMember.setUsername("testuser");
        testMember.setIntegration(1000);

        testParam = new UmsMemberIntegrationParam();
        testParam.setMemberId(1L);
        testParam.setIntegration(100);
        testParam.setOperateNote("测试操作");
    }

    @Test
    public void testAddIntegration() {
        // Given
        when(memberDao.selectByIdForUpdate(1L)).thenReturn(testMember);
        when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
        when(historyMapper.insert(any(UmsIntegrationChangeHistory.class))).thenReturn(1);

        // When
        int result = integrationService.addIntegration(testParam);

        // Then
        assertEquals(1, result);
        verify(memberDao, times(1)).selectByIdForUpdate(1L);
        verify(memberMapper, times(1)).updateByPrimaryKeySelective(argThat(member ->
            member.getIntegration() == 1100
        ));
        verify(historyMapper, times(1)).insert(argThat(history ->
            history.getChangeType() == 0 && history.getChangeCount() == 100
        ));
        verify(redisService, times(1)).del("ums:member:1");
    }

    @Test
    public void testAddIntegration_MemberNotFound() {
        // Given
        when(memberDao.selectByIdForUpdate(1L)).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            integrationService.addIntegration(testParam);
        });
        verify(memberMapper, never()).updateByPrimaryKeySelective(any());
        verify(historyMapper, never()).insert(any());
    }

    @Test
    public void testReduceIntegration() {
        // Given
        when(memberDao.selectByIdForUpdate(1L)).thenReturn(testMember);
        when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
        when(historyMapper.insert(any(UmsIntegrationChangeHistory.class))).thenReturn(1);

        // When
        int result = integrationService.reduceIntegration(testParam);

        // Then
        assertEquals(1, result);
        verify(memberMapper, times(1)).updateByPrimaryKeySelective(argThat(member ->
            member.getIntegration() == 900
        ));
        verify(historyMapper, times(1)).insert(argThat(history ->
            history.getChangeType() == 1 && history.getChangeCount() == 100
        ));
        verify(redisService, times(1)).del("ums:member:1");
    }

    @Test
    public void testReduceIntegration_InsufficientBalance() {
        // Given
        testParam.setIntegration(1500);
        when(memberDao.selectByIdForUpdate(1L)).thenReturn(testMember);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            integrationService.reduceIntegration(testParam);
        });
        verify(memberMapper, never()).updateByPrimaryKeySelective(any());
        verify(historyMapper, never()).insert(any());
    }

    @Test
    public void testGiftIntegration() {
        // Given
        when(memberDao.selectByIdForUpdate(1L)).thenReturn(testMember);
        when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
        when(historyMapper.insert(any(UmsIntegrationChangeHistory.class))).thenReturn(1);

        // When
        int result = integrationService.giftIntegration(testParam);

        // Then
        assertEquals(1, result);
        verify(memberMapper, times(1)).updateByPrimaryKeySelective(argThat(member ->
            member.getIntegration() == 1100
        ));
        verify(historyMapper, times(1)).insert(argThat(history ->
            history.getChangeType() == 0 && history.getSourceType() == 5
        ));
        verify(redisService, times(1)).del("ums:member:1");
    }

    @Test
    public void testFreezeIntegration() {
        // Given
        when(memberDao.selectByIdForUpdate(1L)).thenReturn(testMember);
        when(freezeMapper.insert(any(UmsIntegrationFreeze.class))).thenReturn(1);
        when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
        when(historyMapper.insert(any(UmsIntegrationChangeHistory.class))).thenReturn(1);

        // When
        UmsIntegrationFreeze result = integrationService.freezeIntegration(1L, 100, "ORDER123", 1, "订单冻结");

        // Then
        assertNotNull(result);
        verify(freezeMapper, times(1)).insert(argThat(freeze ->
            freeze.getFreezeAmount() == 100 && freeze.getBusinessId().equals("ORDER123")
        ));
        verify(memberMapper, times(1)).updateByPrimaryKeySelective(argThat(member ->
            member.getIntegration() == 900
        ));
        verify(historyMapper, times(1)).insert(argThat(history ->
            history.getChangeType() == 1 && history.getSourceType() == 9
        ));
        verify(redisService, times(1)).del("ums:member:1");
    }

    @Test
    public void testFreezeIntegration_Idempotent() {
        // Given
        when(memberDao.selectByIdForUpdate(1L)).thenReturn(testMember);
        when(freezeMapper.insert(any(UmsIntegrationFreeze.class)))
            .thenThrow(new DuplicateKeyException("Duplicate key"));

        UmsIntegrationFreeze existingFreeze = new UmsIntegrationFreeze();
        existingFreeze.setId(123L);
        existingFreeze.setStatus(0);
        when(freezeMapper.selectByExample(any())).thenReturn(Collections.singletonList(existingFreeze));

        // When
        UmsIntegrationFreeze result = integrationService.freezeIntegration(1L, 100, "ORDER123", 1, "订单冻结");

        // Then
        assertNotNull(result);
        assertEquals(123L, result.getId());
        verify(freezeMapper, times(1)).selectByExample(any());
    }

    @Test
    public void testFreezeIntegration_InsufficientBalance() {
        // Given
        when(memberDao.selectByIdForUpdate(1L)).thenReturn(testMember);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            integrationService.freezeIntegration(1L, 1500, "ORDER123", 1, "订单冻结");
        });
        verify(freezeMapper, never()).insert(any());
    }

    @Test
    public void testDeductIntegration() {
        // Given
        UmsIntegrationFreeze freeze = new UmsIntegrationFreeze();
        freeze.setMemberId(1L);
        freeze.setFreezeAmount(100);
        freeze.setStatus(0);
        when(freezeMapper.selectByExample(any())).thenReturn(Collections.singletonList(freeze));
        when(freezeMapper.updateByPrimaryKeySelective(any(UmsIntegrationFreeze.class))).thenReturn(1);
        when(historyMapper.insert(any(UmsIntegrationChangeHistory.class))).thenReturn(1);

        // When
        int result = integrationService.deductIntegration("ORDER123", 7);

        // Then
        assertEquals(1, result);
        verify(freezeMapper, times(1)).updateByPrimaryKeySelective(argThat(f ->
            f.getStatus() == 1
        ));
        verify(historyMapper, times(1)).insert(argThat(history ->
            history.getChangeType() == 1 && history.getSourceType() == 7
        ));
    }

    @Test
    public void testDeductIntegration_FreezeNotFound() {
        // Given
        when(freezeMapper.selectByExample(any())).thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            integrationService.deductIntegration("ORDER123", 7);
        });
        verify(freezeMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    public void testDeductIntegration_AlreadyProcessed() {
        // Given
        UmsIntegrationFreeze freeze = new UmsIntegrationFreeze();
        freeze.setStatus(1);
        when(freezeMapper.selectByExample(any())).thenReturn(Collections.singletonList(freeze));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            integrationService.deductIntegration("ORDER123", 7);
        });
        verify(freezeMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    public void testReleaseIntegration() {
        // Given
        UmsIntegrationFreeze freeze = new UmsIntegrationFreeze();
        freeze.setMemberId(1L);
        freeze.setFreezeAmount(100);
        freeze.setStatus(0);
        when(freezeMapper.selectByExample(any())).thenReturn(Collections.singletonList(freeze));
        when(memberDao.selectByIdForUpdate(1L)).thenReturn(testMember);
        when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
        when(freezeMapper.updateByPrimaryKeySelective(any(UmsIntegrationFreeze.class))).thenReturn(1);
        when(historyMapper.insert(any(UmsIntegrationChangeHistory.class))).thenReturn(1);

        // When
        int result = integrationService.releaseIntegration("ORDER123", "订单取消");

        // Then
        assertEquals(1, result);
        verify(memberMapper, times(1)).updateByPrimaryKeySelective(argThat(member ->
            member.getIntegration() == 1100
        ));
        verify(freezeMapper, times(1)).updateByPrimaryKeySelective(argThat(f ->
            f.getStatus() == 2
        ));
        verify(historyMapper, times(1)).insert(argThat(history ->
            history.getChangeType() == 0 && history.getSourceType() == 10
        ));
        verify(redisService, times(1)).del("ums:member:1");
    }

    @Test
    public void testReleaseIntegration_FreezeNotFound() {
        // Given
        when(freezeMapper.selectByExample(any())).thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            integrationService.releaseIntegration("ORDER123", "订单取消");
        });
        verify(memberMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    public void testListIntegration() {
        // Given
        UmsMemberIntegrationQuery query = new UmsMemberIntegrationQuery();
        query.setPageNum(1);
        query.setPageSize(10);
        query.setKeyword("test");

        List<UmsMember> members = Arrays.asList(testMember);
        when(memberMapper.selectByExample(any())).thenReturn(members);

        // When
        List<UmsMemberIntegrationVO> result = integrationService.listIntegration(query);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(memberMapper, times(1)).selectByExample(any());
    }

    @Test
    public void testGetFreezeList() {
        // Given
        UmsIntegrationFreeze freeze = new UmsIntegrationFreeze();
        freeze.setMemberId(1L);
        freeze.setFreezeAmount(100);
        when(freezeMapper.selectByExample(any())).thenReturn(Collections.singletonList(freeze));

        // When
        List<UmsIntegrationFreeze> result = integrationService.getFreezeList(1L, 1, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getFreezeAmount());
        verify(freezeMapper, times(1)).selectByExample(any());
    }

    @Test
    public void testGetHistory() {
        // Given
        UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
        history.setMemberId(1L);
        history.setChangeCount(100);
        when(historyMapper.selectByExample(any())).thenReturn(Collections.singletonList(history));

        // When
        List<UmsIntegrationChangeHistory> result = integrationService.getHistory(1L, 1, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getChangeCount());
        verify(historyMapper, times(1)).selectByExample(any());
    }
}
