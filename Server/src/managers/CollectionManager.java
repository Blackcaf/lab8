package managers;

import models.HumanBeing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CollectionManager {
    private final Map<Long, HumanBeing> collection;
    private final DatabaseManager dbManager;
    private final LocalDateTime initializationDate;

    public CollectionManager(DatabaseManager dbManager) {
        this.collection = new ConcurrentHashMap<>();
        this.dbManager = dbManager;
        this.initializationDate = LocalDateTime.now();
    }

    public List<HumanBeing> getCollection() {
        return dbManager.loadHumanBeings();
    }

    public LocalDateTime getInitializationDate() {
        return initializationDate;
    }

    public boolean add(HumanBeing humanBeing, Integer userId) {
        if (humanBeing == null || userId == null) {
            return false;
        }
        if (dbManager.add(humanBeing, userId)) {
            collection.put(humanBeing.getId(), humanBeing);
            return true;
        }
        return false;
    }

    public boolean update(Long id, HumanBeing humanBeing, Integer userId) {
        return dbManager.updateHumanBeing(id, humanBeing, userId);
    }

    public boolean remove(Long id, Integer userId) {
        return dbManager.removeHumanBeing(id, userId);
    }

    public boolean clear(Integer userId) {
        return dbManager.clearHumanBeings(userId);
    }

    public Map<Long, HumanBeing> getCollectionMap() {
        return collection;
    }
}