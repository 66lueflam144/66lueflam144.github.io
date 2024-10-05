package Crazy.service;

import Crazy.entity.CrazyEntity;

import java.io.IOException;
import java.util.List;

public interface CrazyService {

    public List<CrazyEntity> queryEntity(String userId);

    public void addEntity(CrazyEntity entity);

    public void update(CrazyEntity entity);

    public void delete(CrazyEntity entity);

    public Double getMoney(String productId, String productNums, String userId) throws IOException;
}
