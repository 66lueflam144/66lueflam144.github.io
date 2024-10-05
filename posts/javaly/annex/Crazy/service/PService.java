package Crazy.service;

import Crazy.entity.PEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public interface PService {
    @RequestMapping(value = "/test/item/{productId}")
    @ResponseBody
    public PEntity queryProduct(
            @PathVariable("productId") String productId
    );
}
