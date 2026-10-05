package com.rescuefarm.data;

import com.rescuefarm.data.local.database.CacheMigrationContract;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class CacheMigrationContractTest {
    @Test public void migrationFiveToSixCreatesMetadataOrderAndIndexes() {
        assertEquals(5, CacheMigrationContract.FROM_VERSION);
        assertEquals(6, CacheMigrationContract.TO_VERSION);
        List<String> sql = CacheMigrationContract.version6Statements();
        assertEquals(4, sql.size());
        assertTrue(sql.get(0).contains("cache_metadata"));
        assertTrue(sql.get(1).contains("order_cache"));
        assertTrue(sql.get(2).contains("ownerId"));
        assertTrue(sql.get(3).contains("sellerId"));
        for (String statement : sql) assertFalse(statement.toUpperCase().contains("DROP TABLE"));
    }
}
