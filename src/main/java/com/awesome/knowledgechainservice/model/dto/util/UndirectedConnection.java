package com.awesome.knowledgechainservice.model.dto.util;

import lombok.Data;

import java.util.Objects;

/**
 * 无向关系表示对象
 */
@Data
public class UndirectedConnection<T, F> {
    private T u;
    private F v;

    public static <T, F> UndirectedConnection<T, F> create(T left, F right) {
        UndirectedConnection<T, F> undirectedConnection = new UndirectedConnection<T, F>();
        undirectedConnection.setU(left);
        undirectedConnection.setV(right);
        return undirectedConnection;
    }

    // 使无向关系被视为相同
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UndirectedConnection<?, ?> that = (UndirectedConnection<?, ?>) o;

        // 无向关系：(A,B) 和 (B,A) 被视为相同
        return (Objects.equals(u, that.u) && Objects.equals(v, that.v)) ||
                (Objects.equals(u, that.v) && Objects.equals(v, that.u));
    }

    // 重写 hashCode 方法，确保无向关系生成相同的哈希值
    @Override
    public int hashCode() {
        // 使用排序后的元素生成哈希值，确保 (A,B) 和 (B,A) 生成相同的哈希值
        int leftHash = u != null ? u.hashCode() : 0;
        int rightHash = v != null ? v.hashCode() : 0;
        return leftHash + rightHash; // 加法满足交换律，不依赖顺序
    }
}
