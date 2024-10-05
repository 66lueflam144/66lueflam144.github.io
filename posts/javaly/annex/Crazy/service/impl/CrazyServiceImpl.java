package Crazy.service.impl;

import Crazy.dao.CrazyDao;
import Crazy.entity.CrazyEntity;
import Crazy.entity.PEntity;
import Crazy.service.CrazyService;
import Crazy.service.PService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

@Service
public class CrazyServiceImpl implements CrazyService {

    @Autowired
    private CrazyDao dao = null;

    @Autowired
    private PService pService = null;

    @Autowired
    private ObjectMapper mapper = null;

    @Override
    public List<CrazyEntity> queryEntity(String userId) {
        return this.dao.selectEntity(null, userId,null,null);
    }

    @Override
    public void addEntity(CrazyEntity entity) {
        String productId = entity.getProductId();
        PEntity p = this.pService.queryProduct(productId);
        if (p == null) {
            System.out.println("not exist");
        }

        Integer num = entity.getNum();
        entity.setNum(null);

        List<CrazyEntity> entityList = this.dao.selectEntity(entity);
        if (entityList == null || entityList.size() == 0) {
            entity.setNum(num);
            this.dao.insert(entity);
        }
        else {
            entity.setNum(num);
            this.dao.updateFlush(entity);
        }

        if (entity.getNum() > p.getProductNum()) {
            System.out.println("out of the range!");
        }

    }

    @Override
    public void update(CrazyEntity entity) {
        this.dao.updateFlush(entity);
        String productId = entity.getProductId();
        PEntity p = this.pService.queryProduct(productId);
        if (productId == null) {
            System.out.println("not exist");
        }
        if (entity.getNum() > p.getProductNum()) {
            System.out.println("out of range, bitch!");
        }

    }

    @Override
    public void delete(CrazyEntity entity) {
        this.dao.delete(entity);
    }

    @Override
    public Double getMoney(String productIdss, String productNumss, String userId) throws IOException {
        String[] productIds = this.mapper.readValue(productIdss, String[].class);
        Integer[] productNums = this.mapper.readValue(productNumss, Integer[].class);
        Double money = 0.0;

        if (productIds.length != productNums.length) {
            System.out.println("something goes wrong!");
        }

        for (int i = 0; i < productIds.length; i++) {
            String productId = productIds[i];
            Integer num = productNums[i];
            PEntity p = this.pService.queryProduct(productId);
            if (p == null || num == null) {
                System.out.println("query wrongly.");
            }

            if (num > p.getProductNum()) {
                System.out.println("out of range");
            }
            List<CrazyEntity> entities = this.dao.selectEntity(null, userId, productId, null);
            CrazyEntity entity = entities.get(0);
            money = money + num*entity.getPrice();
        }
        return money;
    }
}
