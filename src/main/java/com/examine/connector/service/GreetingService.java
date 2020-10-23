package com.examine.connector.service;

import com.examine.connector.config.TenantContext;
import com.examine.connector.dao.WorldDao;
import com.examine.connector.entity.World;
import com.examine.connector.tenant.DbType;

public class GreetingService {
    private WorldDao worldDao;

    public GreetingService(WorldDao worldDao) {
        this.worldDao = worldDao;
    }

    public String getGreetingFromFirstDb() {
        TenantContext.set(DbType.FIRST);
        World world = worldDao.getLastWorld();
        return world.getGreeting();
    }

    public String getGreetingFromSecondDb() {
        TenantContext.set(DbType.SECOND);
        World world = worldDao.getLastWorld();
        return world.getGreeting();
    }

}
