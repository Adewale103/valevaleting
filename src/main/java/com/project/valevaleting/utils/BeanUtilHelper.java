package com.project.valevaleting.utils;

import com.project.valevaleting.exception.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.http.HttpStatus;

import java.lang.reflect.InvocationTargetException;
@Slf4j
public class BeanUtilHelper extends BeanUtilsBean {

    /**
     * This code below is a reusable helper that helps to copy class properties from source to destination
     * while ignoring the null fields. This implies that a class with a null field value, the null field
     * is not copied to the destination. This is useful whenever there is a dto is required to accept
     * multiple fields without the fields without validation on all the fields while having some fields
     * with values and other fields without values. This code will filter the null fields and will not
     * copy them to their destination.
     */
    @Override
    public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
        if (value == null) return;
        super.copyProperty(dest, name, value);
    }

    public static void copyPropertiesIgnoreNull(Object source, Object destination){
        try {
            BeanUtilHelper beanUtilHelper = new BeanUtilHelper();
            beanUtilHelper.copyProperties(destination, source);
        }catch (IllegalAccessException | InvocationTargetException e){
            log.error("Error occurred {}", e.getMessage());
            throw new GenericException("Error occurred while processing request", HttpStatus.BAD_REQUEST);
        }
    }
}
