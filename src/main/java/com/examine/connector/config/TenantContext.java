package com.examine.connector.config;

import com.examine.connector.exception.DaoException;
import com.examine.connector.tenant.DbType;

public class TenantContext {
    private static ThreadLocal<DbType> CONTEXT = new ThreadLocal<>();

    public static void set(DbType dbType) {
        if (dbType == null) {
            throw new DaoException("Cannot proceed - DbType is null!");
        }
        CONTEXT.set(dbType);
    }

    public static DbType getCurrentTenant() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
