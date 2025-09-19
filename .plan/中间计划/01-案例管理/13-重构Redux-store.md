# 13-重构Redux-store

## 任务概述
重构用户前端项目中的Redux store，将现有的ai slice中的案例相关逻辑改为从后端API获取真实数据，替换写死的galleryData。

## 前置条件
- 后端案例管理API已完成
- mall-portal案例查询API正常运行
- 用户前端项目可正常访问后端接口

## 实施步骤

### 1. 分析现有Redux结构
**文件路径：** 首先检查现有的Redux store结构

#### 查看现有ai slice
检查 `beilv-agent-web/src/store/slices/ai.ts` 或类似文件中的：
- 现有的projects和cases状态结构
- fetchProjects和fetchCases action
- galleryData的使用方式

### 2. 创建案例API服务
**文件路径：** `beilv-agent-web/src/api/cases.ts`

```typescript
import { request } from '@/utils/request';

// 案例数据接口
export interface CaseData {
  id: number;
  categoryId: number;
  title: string;
  description: string;
  previewImageUrl: string;
  videoUrl: string;
  videoPreviewUrl: string;
  likeCount: number;
  viewCount: number;
  hotScore: number;
  createTime: string;
}

// 案例分类接口
export interface CaseCategory {
  id: number;
  name: string;
  type: 'all' | 'latest' | 'hot' | 'category';
  count?: number;
}

// 案例列表查询参数
export interface CaseListParams {
  categoryType: 'all' | 'latest' | 'hot' | 'category';
  categoryId?: number;
  page: number;
  size: number;
}

// 案例列表查询结果
export interface CaseListResponse {
  list: CaseData[];
  total: number;
  hasMore: boolean;
  currentPage: number;
}

// API服务类
class CasesAPI {
  // 获取完整分类列表（包含虚拟分类）
  async getAllCategories(): Promise<CaseCategory[]> {
    const response = await request.get('/case/categories/all');
    return response.data;
  }

  // 获取案例列表（支持懒加载）
  async getCaseList(params: CaseListParams): Promise<CaseListResponse> {
    const response = await request.get('/case/list', { params });
    return response.data;
  }

  // 获取案例详情
  async getCaseDetail(id: number): Promise<CaseData> {
    const response = await request.get(`/case/detail/${id}`);
    return response.data;
  }

  // 增加浏览量
  async incrementView(id: number): Promise<void> {
    await request.post(`/case/view/${id}`);
  }

  // 点赞/取消点赞
  async toggleLike(id: number): Promise<{ liked: boolean; likeCount: number }> {
    const response = await request.post(`/case/like/${id}`);
    return response.data;
  }
}

export const casesAPI = new CasesAPI();
```

### 3. 更新Redux store类型定义
**文件路径：** `beilv-agent-web/src/types/store.ts`

```typescript
export interface CasesState {
  // 分类数据
  categories: CaseCategory[];
  activeCategory: string; // 当前选中的分类类型
  
  // 案例列表数据
  cases: CaseData[];
  loading: boolean;
  error: string | null;
  
  // 分页信息
  currentPage: number;
  pageSize: number;
  total: number;
  hasMore: boolean;
  
  // UI状态
  isInitialized: boolean;
  isLoadingMore: boolean;
}

export interface AIState extends CasesState {
  // 保留原有的projects相关状态
  projects: any[];
  // ... 其他ai相关状态
}
```

### 4. 重构ai slice
**文件路径：** `beilv-agent-web/src/store/slices/ai.ts`

```typescript
import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { casesAPI, CaseData, CaseCategory, CaseListParams } from '@/api/cases';
import { AIState } from '@/types/store';

// 异步action：获取分类列表
export const fetchCategories = createAsyncThunk(
  'ai/fetchCategories',
  async () => {
    const categories = await casesAPI.getAllCategories();
    return categories;
  }
);

// 异步action：获取案例列表
export const fetchCases = createAsyncThunk(
  'ai/fetchCases',
  async (params: CaseListParams) => {
    const response = await casesAPI.getCaseList(params);
    return { ...response, isLoadMore: params.page > 1 };
  }
);

// 异步action：点赞操作
export const toggleCaseLike = createAsyncThunk(
  'ai/toggleCaseLike',
  async (caseId: number) => {
    const result = await casesAPI.toggleLike(caseId);
    return { caseId, ...result };
  }
);

// 异步action：增加浏览量
export const incrementCaseView = createAsyncThunk(
  'ai/incrementCaseView',
  async (caseId: number) => {
    await casesAPI.incrementView(caseId);
    return caseId;
  }
);

const initialState: AIState = {
  // 案例相关状态
  categories: [],
  activeCategory: 'all',
  cases: [],
  loading: false,
  error: null,
  currentPage: 1,
  pageSize: 20,
  total: 0,
  hasMore: true,
  isInitialized: false,
  isLoadingMore: false,
  
  // 保留原有projects状态
  projects: [],
  // ... 其他原有状态
};

const aiSlice = createSlice({
  name: 'ai',
  initialState,
  reducers: {
    // 设置当前分类
    setActiveCategory: (state, action: PayloadAction<string>) => {
      state.activeCategory = action.payload;
      // 重置分页状态
      state.cases = [];
      state.currentPage = 1;
      state.hasMore = true;
    },
    
    // 重置案例列表
    resetCases: (state) => {
      state.cases = [];
      state.currentPage = 1;
      state.hasMore = true;
      state.loading = false;
      state.isLoadingMore = false;
    },
    
    // 清除错误
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    // 处理获取分类列表
    builder
      .addCase(fetchCategories.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCategories.fulfilled, (state, action) => {
        state.loading = false;
        state.categories = action.payload;
        state.isInitialized = true;
      })
      .addCase(fetchCategories.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '获取分类失败';
      });
    
    // 处理获取案例列表
    builder
      .addCase(fetchCases.pending, (state) => {
        if (state.currentPage === 1) {
          state.loading = true;
        } else {
          state.isLoadingMore = true;
        }
        state.error = null;
      })
      .addCase(fetchCases.fulfilled, (state, action) => {
        const { list, total, hasMore, currentPage, isLoadMore } = action.payload;
        
        state.loading = false;
        state.isLoadingMore = false;
        
        if (isLoadMore) {
          // 追加数据（懒加载）
          state.cases.push(...list);
        } else {
          // 替换数据（首次加载或切换分类）
          state.cases = list;
        }
        
        state.total = total;
        state.hasMore = hasMore;
        state.currentPage = currentPage;
      })
      .addCase(fetchCases.rejected, (state, action) => {
        state.loading = false;
        state.isLoadingMore = false;
        state.error = action.error.message || '获取案例失败';
      });
    
    // 处理点赞操作
    builder
      .addCase(toggleCaseLike.fulfilled, (state, action) => {
        const { caseId, likeCount } = action.payload;
        const caseIndex = state.cases.findIndex(c => c.id === caseId);
        if (caseIndex >= 0) {
          state.cases[caseIndex].likeCount = likeCount;
        }
      });
    
    // 处理浏览量增加
    builder
      .addCase(incrementCaseView.fulfilled, (state, action) => {
        const caseId = action.payload;
        const caseIndex = state.cases.findIndex(c => c.id === caseId);
        if (caseIndex >= 0) {
          state.cases[caseIndex].viewCount += 1;
        }
      });
  },
});

export const { setActiveCategory, resetCases, clearError } = aiSlice.actions;
export default aiSlice.reducer;
```

### 5. 创建自定义Hook
**文件路径：** `beilv-agent-web/src/hooks/useCases.ts`

```typescript
import { useCallback, useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '@/hooks/redux';
import {
  fetchCategories,
  fetchCases,
  setActiveCategory,
  resetCases,
  toggleCaseLike,
  incrementCaseView
} from '@/store/slices/ai';

export const useCases = () => {
  const dispatch = useAppDispatch();
  const {
    categories,
    activeCategory,
    cases,
    loading,
    error,
    currentPage,
    hasMore,
    isInitialized,
    isLoadingMore
  } = useAppSelector(state => state.ai);

  // 初始化数据
  const initialize = useCallback(() => {
    if (!isInitialized) {
      dispatch(fetchCategories());
    }
  }, [dispatch, isInitialized]);

  // 切换分类
  const changeCategory = useCallback((categoryType: string) => {
    dispatch(setActiveCategory(categoryType));
  }, [dispatch]);

  // 加载案例列表
  const loadCases = useCallback((page = 1) => {
    const categoryInfo = categories.find(c => c.type === activeCategory);
    
    dispatch(fetchCases({
      categoryType: activeCategory as any,
      categoryId: categoryInfo?.id,
      page,
      size: 20
    }));
  }, [dispatch, activeCategory, categories]);

  // 加载更多
  const loadMore = useCallback(() => {
    if (hasMore && !isLoadingMore) {
      loadCases(currentPage + 1);
    }
  }, [loadCases, hasMore, isLoadingMore, currentPage]);

  // 点赞
  const handleLike = useCallback((caseId: number) => {
    dispatch(toggleCaseLike(caseId));
  }, [dispatch]);

  // 增加浏览量
  const handleView = useCallback((caseId: number) => {
    dispatch(incrementCaseView(caseId));
  }, [dispatch]);

  // 重置数据
  const reset = useCallback(() => {
    dispatch(resetCases());
  }, [dispatch]);

  return {
    // 数据
    categories,
    activeCategory,
    cases,
    loading,
    error,
    hasMore,
    isLoadingMore,
    
    // 方法
    initialize,
    changeCategory,
    loadCases,
    loadMore,
    handleLike,
    handleView,
    reset
  };
};
```

### 6. 移除galleryData依赖
删除或重命名现有的 `galleryData` 文件，确保不再使用写死的数据。

## 验证步骤
1. 编译前端项目确认无语法错误
2. 测试Redux store的数据流
3. 验证API调用是否正常
4. 确认分类切换和数据加载功能

## 迁移说明
1. **保持向后兼容**：保留原有的projects相关逻辑
2. **渐进式迁移**：先替换cases相关逻辑，projects后续再处理
3. **错误处理**：添加完善的错误处理和loading状态
4. **类型安全**：使用TypeScript确保类型安全

## 输出物
- 重构后的ai slice
- 案例API服务类
- 自定义useCases Hook
- 完整的类型定义
- 移除galleryData依赖

## 后续任务
- 下一步：14-实现懒加载组件.md