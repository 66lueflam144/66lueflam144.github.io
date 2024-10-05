package Crazy.dao;

import Crazy.entity.CrazyEntity;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CrazyDao {
    public List<CrazyEntity> selectEntity(CrazyEntity entity);

    public List<CrazyEntity> selectEntity(
            @Param("ID") Integer ID,
            @Param("userId") String userId,
            @Param("productId") String productId,
            @Param("num") Integer num
    );

    public void insert(CrazyEntity entity);

    public void updateEntity(CrazyEntity entity);

    public void updateFlush(CrazyEntity entity);

    public void delete(CrazyEntity entity);
}
