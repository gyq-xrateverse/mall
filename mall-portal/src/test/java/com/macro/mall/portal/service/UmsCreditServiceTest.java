package com.macro.mall.portal.service;

import com.macro.mall.common.exception.ApiException;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.mapper.UmsIntegrationFreezeMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsIntegrationFreeze;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.impl.UmsCreditServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UmsCreditService单元测试
 * 测试覆盖积分冻结、扣减、释放和余额查询功能
 *
 * @author code-executor
 * @since 2026-01-05
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("积分服务单元测试")
public class UmsCreditServiceTest {

    @Mock
    private UmsIntegrationFreezeMapper freezeMapper;

    @Mock
    private UmsMemberMapper memberMapper;

    @Mock
    private UmsMemberCacheService memberCacheService;

    @InjectMocks
    private UmsCreditServiceImpl creditService;

    // 测试数据常量
    private static final Long TEST_MEMBER_ID = 1L;
    private static final String TEST_BUSINESS_ID = "test-business-123";
    private static final String TEST_BUSINESS_TYPE = "ai_task";
    private static final Integer TEST_FREEZE_AMOUNT = 100;
    private static final Integer TEST_CURRENT_INTEGRATION = 500;

    private UmsMember testMember;
    private UmsIntegrationFreeze testFreeze;
    private CreditFreezeRequest freezeRequest;

    @BeforeEach
    void setUp() {
        // 准备测试用户
        testMember = new UmsMember();
        testMember.setId(TEST_MEMBER_ID);
        testMember.setIntegration(TEST_CURRENT_INTEGRATION);
        testMember.setUsername("test_user");

        // 准备测试冻结记录
        testFreeze = new UmsIntegrationFreeze();
        testFreeze.setId(1L);
        testFreeze.setMemberId(TEST_MEMBER_ID);
        testFreeze.setFreezeAmount(TEST_FREEZE_AMOUNT);
        testFreeze.setBusinessId(TEST_BUSINESS_ID);
        testFreeze.setBusinessType(TEST_BUSINESS_TYPE);
        testFreeze.setStatus(UmsIntegrationFreeze.Status.FROZEN);
        testFreeze.setCreateTime(new Date());

        // 准备冻结请求
        freezeRequest = new CreditFreezeRequest();
        freezeRequest.setMemberId(TEST_MEMBER_ID);
        freezeRequest.setFreezeAmount(TEST_FREEZE_AMOUNT);
        freezeRequest.setBusinessId(TEST_BUSINESS_ID);
        freezeRequest.setBusinessType(TEST_BUSINESS_TYPE);
        freezeRequest.setNote("测试冻结");
    }

    @Nested
    @DisplayName("1. 积分冻结测试")
    class FreezeCreditTest {

        @Test
        @DisplayName("冻结积分成功 - 正常流程")
        void testFreezeCreditSuccess() {
            // Given
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(null);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(testMember);
            when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
            when(freezeMapper.insert(any(UmsIntegrationFreeze.class))).thenReturn(1);
            doNothing().when(memberCacheService).delMember(TEST_MEMBER_ID);

            // When
            Long freezeId = creditService.freezeCredit(freezeRequest);

            // Then
            assertNotNull(freezeId);
            verify(freezeMapper).selectByBusinessId(TEST_BUSINESS_ID);
            verify(memberMapper).selectByPrimaryKeyForUpdate(TEST_MEMBER_ID);
            verify(memberMapper).updateByPrimaryKeySelective(argThat(member ->
                    member.getIntegration() == TEST_CURRENT_INTEGRATION - TEST_FREEZE_AMOUNT
            ));
            verify(freezeMapper).insert(any(UmsIntegrationFreeze.class));
            verify(memberCacheService).delMember(TEST_MEMBER_ID);
        }

        @Test
        @DisplayName("冻结积分成功 - 幂等性返回已有记录")
        void testFreezeCreditIdempotent() {
            // Given - 记录已存在
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(testFreeze);

            // When
            Long freezeId = creditService.freezeCredit(freezeRequest);

            // Then
            assertEquals(testFreeze.getId(), freezeId);
            verify(freezeMapper).selectByBusinessId(TEST_BUSINESS_ID);
            verify(memberMapper, never()).selectByPrimaryKeyForUpdate(anyLong());
            verify(memberMapper, never()).updateByPrimaryKeySelective(any());
            verify(freezeMapper, never()).insert(any());
        }

        @Test
        @DisplayName("冻结积分失败 - 用户不存在")
        void testFreezeCreditUserNotFound() {
            // Given
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(null);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(null);

            // When & Then
            ApiException exception = assertThrows(ApiException.class, () ->
                    creditService.freezeCredit(freezeRequest)
            );
            assertEquals("用户不存在", exception.getMessage());
            verify(memberMapper, never()).updateByPrimaryKeySelective(any());
            verify(freezeMapper, never()).insert(any());
        }

        @Test
        @DisplayName("冻结积分失败 - 积分余额不足")
        void testFreezeCreditInsufficientBalance() {
            // Given - 余额不足
            testMember.setIntegration(50); // 小于冻结金额100
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(null);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(testMember);

            // When & Then
            ApiException exception = assertThrows(ApiException.class, () ->
                    creditService.freezeCredit(freezeRequest)
            );
            assertEquals("积分余额不足", exception.getMessage());
            verify(memberMapper, never()).updateByPrimaryKeySelective(any());
            verify(freezeMapper, never()).insert(any());
        }

        @Test
        @DisplayName("冻结积分失败 - 用户积分为null")
        void testFreezeCreditNullIntegration() {
            // Given
            testMember.setIntegration(null);
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(null);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(testMember);

            // When & Then
            ApiException exception = assertThrows(ApiException.class, () ->
                    creditService.freezeCredit(freezeRequest)
            );
            assertEquals("积分余额不足", exception.getMessage());
        }

        @Test
        @DisplayName("冻结积分失败 - 更新用户积分失败")
        void testFreezeCreditUpdateMemberFailed() {
            // Given
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(null);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(testMember);
            when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(0);

            // When & Then
            ApiException exception = assertThrows(ApiException.class, () ->
                    creditService.freezeCredit(freezeRequest)
            );
            assertEquals("更新用户积分失败", exception.getMessage());
            verify(freezeMapper, never()).insert(any());
        }

        @Test
        @DisplayName("冻结积分失败 - 插入冻结记录失败")
        void testFreezeCreditInsertFreezeFailed() {
            // Given
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(null);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(testMember);
            when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
            when(freezeMapper.insert(any(UmsIntegrationFreeze.class))).thenReturn(0);

            // When & Then
            ApiException exception = assertThrows(ApiException.class, () ->
                    creditService.freezeCredit(freezeRequest)
            );
            assertEquals("插入冻结记录失败", exception.getMessage());
        }

        @Test
        @DisplayName("冻结积分成功 - 缓存清除失败不影响主流程")
        void testFreezeCreditCacheDeleteFailed() {
            // Given
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(null);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(testMember);
            when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
            when(freezeMapper.insert(any(UmsIntegrationFreeze.class))).thenReturn(1);
            doThrow(new RuntimeException("Redis连接失败")).when(memberCacheService).delMember(TEST_MEMBER_ID);

            // When - 应该成功执行，缓存失败不影响主流程
            Long freezeId = creditService.freezeCredit(freezeRequest);

            // Then
            assertNotNull(freezeId);
            verify(memberCacheService).delMember(TEST_MEMBER_ID);
        }
    }

    @Nested
    @DisplayName("2. 积分扣减测试")
    class DeductCreditTest {

        private CreditDeductRequest deductRequest;

        @BeforeEach
        void setUp() {
            deductRequest = new CreditDeductRequest();
            deductRequest.setBusinessId(TEST_BUSINESS_ID);
            deductRequest.setNote("测试扣减");
        }

        @Test
        @DisplayName("扣减积分成功 - 正常流程")
        void testDeductCreditSuccess() {
            // Given
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(testFreeze);
            when(freezeMapper.updateByPrimaryKeySelective(any(UmsIntegrationFreeze.class))).thenReturn(1);

            // When
            Boolean result = creditService.deductCredit(deductRequest);

            // Then
            assertTrue(result);
            verify(freezeMapper).selectByBusinessId(TEST_BUSINESS_ID);
            verify(freezeMapper).updateByPrimaryKeySelective(argThat(freeze ->
                    freeze.getStatus() == UmsIntegrationFreeze.Status.DEDUCTED &&
                            freeze.getDeductTime() != null
            ));
        }

        @Test
        @DisplayName("扣减积分成功 - 幂等性已扣减")
        void testDeductCreditIdempotent() {
            // Given - 已扣减
            testFreeze.setStatus(UmsIntegrationFreeze.Status.DEDUCTED);
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(testFreeze);

            // When
            Boolean result = creditService.deductCredit(deductRequest);

            // Then
            assertTrue(result);
            verify(freezeMapper).selectByBusinessId(TEST_BUSINESS_ID);
            verify(freezeMapper, never()).updateByPrimaryKeySelective(any());
        }

        @Test
        @DisplayName("扣减积分失败 - 冻结记录不存在")
        void testDeductCreditFreezeNotFound() {
            // Given
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(null);

            // When & Then
            ApiException exception = assertThrows(ApiException.class, () ->
                    creditService.deductCredit(deductRequest)
            );
            assertEquals("冻结记录不存在", exception.getMessage());
            verify(freezeMapper, never()).updateByPrimaryKeySelective(any());
        }

        @Test
        @DisplayName("扣减积分失败 - 冻结记录状态异常（已释放）")
        void testDeductCreditInvalidStatusReleased() {
            // Given
            testFreeze.setStatus(UmsIntegrationFreeze.Status.RELEASED);
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(testFreeze);

            // When & Then
            ApiException exception = assertThrows(ApiException.class, () ->
                    creditService.deductCredit(deductRequest)
            );
            assertTrue(exception.getMessage().contains("冻结记录状态异常"));
            verify(freezeMapper, never()).updateByPrimaryKeySelective(any());
        }

        @Test
        @DisplayName("扣减积分失败 - 更新冻结记录失败")
        void testDeductCreditUpdateFailed() {
            // Given
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(testFreeze);
            when(freezeMapper.updateByPrimaryKeySelective(any(UmsIntegrationFreeze.class))).thenReturn(0);

            // When & Then
            ApiException exception = assertThrows(ApiException.class, () ->
                    creditService.deductCredit(deductRequest)
            );
            assertEquals("更新冻结记录失败", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("3. 积分释放测试")
    class UnfreezeCreditTest {

        private CreditUnfreezeRequest unfreezeRequest;

        @BeforeEach
        void setUp() {
            unfreezeRequest = new CreditUnfreezeRequest();
            unfreezeRequest.setBusinessId(TEST_BUSINESS_ID);
            unfreezeRequest.setReason("任务失败，释放积分");
        }

        @Test
        @DisplayName("释放积分成功 - 正常流程")
        void testUnfreezeCreditSuccess() {
            // Given
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(testFreeze);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(testMember);
            when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
            when(freezeMapper.updateByPrimaryKeySelective(any(UmsIntegrationFreeze.class))).thenReturn(1);
            doNothing().when(memberCacheService).delMember(TEST_MEMBER_ID);

            // When
            Boolean result = creditService.unfreezeCredit(unfreezeRequest);

            // Then
            assertTrue(result);
            verify(freezeMapper).selectByBusinessId(TEST_BUSINESS_ID);
            verify(memberMapper).selectByPrimaryKeyForUpdate(TEST_MEMBER_ID);
            verify(memberMapper).updateByPrimaryKeySelective(argThat(member ->
                    member.getIntegration() == TEST_CURRENT_INTEGRATION + TEST_FREEZE_AMOUNT
            ));
            verify(freezeMapper).updateByPrimaryKeySelective(argThat(freeze ->
                    freeze.getStatus() == UmsIntegrationFreeze.Status.RELEASED &&
                            freeze.getReleaseTime() != null
            ));
            verify(memberCacheService).delMember(TEST_MEMBER_ID);
        }

        @Test
        @DisplayName("释放积分成功 - 幂等性已释放")
        void testUnfreezeCreditIdempotent() {
            // Given - 已释放
            testFreeze.setStatus(UmsIntegrationFreeze.Status.RELEASED);
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(testFreeze);

            // When
            Boolean result = creditService.unfreezeCredit(unfreezeRequest);

            // Then
            assertTrue(result);
            verify(freezeMapper).selectByBusinessId(TEST_BUSINESS_ID);
            verify(memberMapper, never()).selectByPrimaryKeyForUpdate(anyLong());
        }

        @Test
        @DisplayName("释放积分失败 - 冻结记录不存在")
        void testUnfreezeCreditFreezeNotFound() {
            // Given
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(null);

            // When & Then
            ApiException exception = assertThrows(ApiException.class, () ->
                    creditService.unfreezeCredit(unfreezeRequest)
            );
            assertEquals("冻结记录不存在", exception.getMessage());
        }

        @Test
        @DisplayName("释放积分失败 - 冻结记录状态异常（已扣减）")
        void testUnfreezeCreditInvalidStatusDeducted() {
            // Given
            testFreeze.setStatus(UmsIntegrationFreeze.Status.DEDUCTED);
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(testFreeze);

            // When & Then
            ApiException exception = assertThrows(ApiException.class, () ->
                    creditService.unfreezeCredit(unfreezeRequest)
            );
            assertTrue(exception.getMessage().contains("冻结记录状态异常"));
        }

        @Test
        @DisplayName("释放积分失败 - 用户不存在")
        void testUnfreezeCreditUserNotFound() {
            // Given
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(testFreeze);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(null);

            // When & Then
            ApiException exception = assertThrows(ApiException.class, () ->
                    creditService.unfreezeCredit(unfreezeRequest)
            );
            assertEquals("用户不存在", exception.getMessage());
        }

        @Test
        @DisplayName("释放积分成功 - 用户积分为null时正确处理")
        void testUnfreezeCreditNullIntegration() {
            // Given
            testMember.setIntegration(null);
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(testFreeze);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(testMember);
            when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
            when(freezeMapper.updateByPrimaryKeySelective(any(UmsIntegrationFreeze.class))).thenReturn(1);
            doNothing().when(memberCacheService).delMember(TEST_MEMBER_ID);

            // When
            Boolean result = creditService.unfreezeCredit(unfreezeRequest);

            // Then
            assertTrue(result);
            verify(memberMapper).updateByPrimaryKeySelective(argThat(member ->
                    member.getIntegration() == TEST_FREEZE_AMOUNT // 0 + TEST_FREEZE_AMOUNT
            ));
        }

        @Test
        @DisplayName("释放积分失败 - 恢复用户积分失败")
        void testUnfreezeCreditRestoreMemberFailed() {
            // Given
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(testFreeze);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(testMember);
            when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(0);

            // When & Then
            ApiException exception = assertThrows(ApiException.class, () ->
                    creditService.unfreezeCredit(unfreezeRequest)
            );
            assertEquals("恢复用户积分失败", exception.getMessage());
            verify(freezeMapper, never()).updateByPrimaryKeySelective(any());
        }

        @Test
        @DisplayName("释放积分失败 - 更新冻结记录失败")
        void testUnfreezeCreditUpdateFreezeFailed() {
            // Given
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(testFreeze);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(testMember);
            when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
            when(freezeMapper.updateByPrimaryKeySelective(any(UmsIntegrationFreeze.class))).thenReturn(0);

            // When & Then
            ApiException exception = assertThrows(ApiException.class, () ->
                    creditService.unfreezeCredit(unfreezeRequest)
            );
            assertEquals("更新冻结记录失败", exception.getMessage());
        }

        @Test
        @DisplayName("释放积分成功 - 缓存清除失败不影响主流程")
        void testUnfreezeCreditCacheDeleteFailed() {
            // Given
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(testFreeze);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(testMember);
            when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
            when(freezeMapper.updateByPrimaryKeySelective(any(UmsIntegrationFreeze.class))).thenReturn(1);
            doThrow(new RuntimeException("Redis连接失败")).when(memberCacheService).delMember(TEST_MEMBER_ID);

            // When - 应该成功执行，缓存失败不影响主流程
            Boolean result = creditService.unfreezeCredit(unfreezeRequest);

            // Then
            assertTrue(result);
            verify(memberCacheService).delMember(TEST_MEMBER_ID);
        }
    }

    @Nested
    @DisplayName("4. 积分余额查询测试")
    class GetBalanceTest {

        @Test
        @DisplayName("查询余额成功 - 正常情况")
        void testGetBalanceSuccess() {
            // Given
            when(memberMapper.selectByPrimaryKey(TEST_MEMBER_ID)).thenReturn(testMember);
            when(freezeMapper.sumFrozenByMemberId(TEST_MEMBER_ID)).thenReturn(TEST_FREEZE_AMOUNT);

            // When
            CreditBalanceResult result = creditService.getBalance(TEST_MEMBER_ID);

            // Then
            assertNotNull(result);
            assertEquals(TEST_MEMBER_ID, result.getMemberId());
            assertEquals(TEST_CURRENT_INTEGRATION, result.getTotalIntegration());
            assertEquals(TEST_FREEZE_AMOUNT, result.getFrozenIntegration());
            assertEquals(TEST_CURRENT_INTEGRATION - TEST_FREEZE_AMOUNT, result.getAvailableIntegration());
            verify(memberMapper).selectByPrimaryKey(TEST_MEMBER_ID);
            verify(freezeMapper).sumFrozenByMemberId(TEST_MEMBER_ID);
        }

        @Test
        @DisplayName("查询余额成功 - 无冻结积分")
        void testGetBalanceNoFrozen() {
            // Given
            when(memberMapper.selectByPrimaryKey(TEST_MEMBER_ID)).thenReturn(testMember);
            when(freezeMapper.sumFrozenByMemberId(TEST_MEMBER_ID)).thenReturn(null);

            // When
            CreditBalanceResult result = creditService.getBalance(TEST_MEMBER_ID);

            // Then
            assertNotNull(result);
            assertEquals(TEST_CURRENT_INTEGRATION, result.getTotalIntegration());
            assertEquals(0, result.getFrozenIntegration());
            assertEquals(TEST_CURRENT_INTEGRATION, result.getAvailableIntegration());
        }

        @Test
        @DisplayName("查询余额成功 - 用户积分为null")
        void testGetBalanceNullIntegration() {
            // Given
            testMember.setIntegration(null);
            when(memberMapper.selectByPrimaryKey(TEST_MEMBER_ID)).thenReturn(testMember);
            when(freezeMapper.sumFrozenByMemberId(TEST_MEMBER_ID)).thenReturn(0);

            // When
            CreditBalanceResult result = creditService.getBalance(TEST_MEMBER_ID);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getTotalIntegration());
            assertEquals(0, result.getFrozenIntegration());
            assertEquals(0, result.getAvailableIntegration());
        }

        @Test
        @DisplayName("查询余额失败 - 用户不存在")
        void testGetBalanceUserNotFound() {
            // Given
            when(memberMapper.selectByPrimaryKey(TEST_MEMBER_ID)).thenReturn(null);

            // When & Then
            ApiException exception = assertThrows(ApiException.class, () ->
                    creditService.getBalance(TEST_MEMBER_ID)
            );
            assertEquals("用户不存在", exception.getMessage());
            verify(freezeMapper, never()).sumFrozenByMemberId(anyLong());
        }

        @Test
        @DisplayName("查询余额成功 - 全部冻结")
        void testGetBalanceAllFrozen() {
            // Given
            when(memberMapper.selectByPrimaryKey(TEST_MEMBER_ID)).thenReturn(testMember);
            when(freezeMapper.sumFrozenByMemberId(TEST_MEMBER_ID)).thenReturn(TEST_CURRENT_INTEGRATION);

            // When
            CreditBalanceResult result = creditService.getBalance(TEST_MEMBER_ID);

            // Then
            assertEquals(TEST_CURRENT_INTEGRATION, result.getTotalIntegration());
            assertEquals(TEST_CURRENT_INTEGRATION, result.getFrozenIntegration());
            assertEquals(0, result.getAvailableIntegration());
        }
    }

    @Nested
    @DisplayName("5. 边界条件和特殊场景测试")
    class EdgeCaseTest {

        @Test
        @DisplayName("冻结最小金额（1积分）")
        void testFreezeMinimumAmount() {
            // Given
            freezeRequest.setFreezeAmount(1);
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(null);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(testMember);
            when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
            when(freezeMapper.insert(any(UmsIntegrationFreeze.class))).thenReturn(1);
            doNothing().when(memberCacheService).delMember(TEST_MEMBER_ID);

            // When
            Long freezeId = creditService.freezeCredit(freezeRequest);

            // Then
            assertNotNull(freezeId);
            verify(memberMapper).updateByPrimaryKeySelective(argThat(member ->
                    member.getIntegration() == TEST_CURRENT_INTEGRATION - 1
            ));
        }

        @Test
        @DisplayName("冻结所有可用积分")
        void testFreezeAllAvailable() {
            // Given
            freezeRequest.setFreezeAmount(TEST_CURRENT_INTEGRATION);
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(null);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(testMember);
            when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
            when(freezeMapper.insert(any(UmsIntegrationFreeze.class))).thenReturn(1);
            doNothing().when(memberCacheService).delMember(TEST_MEMBER_ID);

            // When
            Long freezeId = creditService.freezeCredit(freezeRequest);

            // Then
            assertNotNull(freezeId);
            verify(memberMapper).updateByPrimaryKeySelective(argThat(member ->
                    member.getIntegration() == 0
            ));
        }

        @Test
        @DisplayName("冻结大额积分")
        void testFreezeLargeAmount() {
            // Given
            int largeAmount = 999999;
            testMember.setIntegration(1000000);
            freezeRequest.setFreezeAmount(largeAmount);
            when(freezeMapper.selectByBusinessId(TEST_BUSINESS_ID)).thenReturn(null);
            when(memberMapper.selectByPrimaryKeyForUpdate(TEST_MEMBER_ID)).thenReturn(testMember);
            when(memberMapper.updateByPrimaryKeySelective(any(UmsMember.class))).thenReturn(1);
            when(freezeMapper.insert(any(UmsIntegrationFreeze.class))).thenReturn(1);
            doNothing().when(memberCacheService).delMember(TEST_MEMBER_ID);

            // When
            Long freezeId = creditService.freezeCredit(freezeRequest);

            // Then
            assertNotNull(freezeId);
            verify(memberMapper).updateByPrimaryKeySelective(argThat(member ->
                    member.getIntegration() == 1
            ));
        }

        @Test
        @DisplayName("多次查询余额 - 验证一致性")
        void testGetBalanceConsistency() {
            // Given
            when(memberMapper.selectByPrimaryKey(TEST_MEMBER_ID)).thenReturn(testMember);
            when(freezeMapper.sumFrozenByMemberId(TEST_MEMBER_ID)).thenReturn(TEST_FREEZE_AMOUNT);

            // When - 多次查询
            CreditBalanceResult result1 = creditService.getBalance(TEST_MEMBER_ID);
            CreditBalanceResult result2 = creditService.getBalance(TEST_MEMBER_ID);

            // Then - 结果应该一致
            assertEquals(result1.getTotalIntegration(), result2.getTotalIntegration());
            assertEquals(result1.getFrozenIntegration(), result2.getFrozenIntegration());
            assertEquals(result1.getAvailableIntegration(), result2.getAvailableIntegration());
        }
    }
}
