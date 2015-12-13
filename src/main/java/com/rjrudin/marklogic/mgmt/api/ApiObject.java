package com.rjrudin.marklogic.mgmt.api;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Base class for any object used for ferrying JSON around with Jackson.
 */
@JsonIgnoreProperties
public abstract class ApiObject {

    /**
     * Intended as a convenience method for a user to find out all the property names of an API object from within the
     * context of e.g. Groovy Shell.
     * 
     * @return
     */
    public List<String> getPropertyNames() {
        BeanWrapper bw = new BeanWrapperImpl(this);
        List<String> list = new ArrayList<>();
        for (PropertyDescriptor pd : bw.getPropertyDescriptors()) {
            if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
                list.add(pd.getName());
            }
        }
        return list;
    }

}
