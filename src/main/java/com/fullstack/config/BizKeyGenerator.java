package com.fullstack.config;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.query.Query;

public class BizKeyGenerator implements IdentifierGenerator {
    
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        try {
            // Query to get the maximum bizKey value
            String sql = "SELECT COALESCE(MAX(CAST(biz_key AS INTEGER)), 0) FROM users_infm";
            Query<Number> query = session.createNativeQuery(sql, Number.class);
            Number maxValue = query.uniqueResult();
            
            // Increment the max value
            long nextValue = maxValue.longValue() + 1;
            
            return String.format("%05d", nextValue); // Format as 5-digit zero-padded string
        } catch (Exception e) {
            // Fallback: if there's any error, start with 1
            return "00001";
        }
    }
} 