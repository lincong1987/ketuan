package com.skqtec.repository;

import com.alibaba.fastjson.JSONObject;
import com.skqtec.entity.OrderEntity;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    static Logger logger = Logger.getLogger(OrderRepositoryImpl.class.getName());
    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return this.sessionFactory.openSession();
    }

    public OrderEntity load(String id) {
        return (OrderEntity)getCurrentSession().load(OrderEntity.class,id);
    }

    public OrderEntity get(String id) {
        return (OrderEntity)getCurrentSession().get(OrderEntity.class,id);
    }

    public List<OrderEntity> findAll() {
        return getCurrentSession().createQuery("from "+OrderEntity.class.getSimpleName()).list();
    }

    public List<OrderEntity> query(JSONObject jsonObject) {
        Session s = null;
        List<OrderEntity> list = new ArrayList<OrderEntity>();
        try {
            s = getCurrentSession();
            Criteria c = s.createCriteria(OrderEntity.class);
            Iterator it = jsonObject.keySet().iterator();
            while (it.hasNext()){
                String key = (String)it.next();
                String value = jsonObject.getString(key);
                c.add(Restrictions.eq(key, value));
            }
            list = c.list();
            for (OrderEntity Order : list) {
                //System.out.println(Order.getName());
            }
        }catch (Exception e){
            logger.error(e);
        }
        finally {
            if (s != null)
                s.close();
            return list;
        }
    }

    public List<OrderEntity> search(String key) {
        Session s = null;
        List<OrderEntity> list = new ArrayList<OrderEntity>();
        try {
            s = getCurrentSession();
            Query q = s.createSQLQuery("SELECT * FROM `ORDER` as a where a.name like '%"+key+"%' and a.discription like '%"+key+"%'").addEntity(OrderEntity.class);
            list = q.list();
        }
        catch(Exception e){
            logger.error(e.getMessage(),e);
        }
        finally {
            if (s != null)
                s.close();
            return list;
        }
    }
    public List<OrderEntity>query(String userId,String orderState){
        Session s = null;
        List<OrderEntity> list = new ArrayList<OrderEntity>();
        try {
            int state=Integer.parseInt(orderState);
            s = getCurrentSession();
            Query q = s.createSQLQuery("SELECT * FROM `ORDER` as a where a.user_id="+userId+" and a.state="+state).addEntity(OrderEntity.class);
            list = q.list();
        }
        catch(Exception e){
            logger.error(e.getMessage(),e);
        }
        finally {
            if (s != null)
                s.close();
            return list;
        }
    }

    public void persist(OrderEntity entity) {
        Session session = getCurrentSession();
        session.persist(entity);
    }

    public String save(OrderEntity entity) {
        Session session = null;
        session=getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Serializable pKey = session.save(entity);

        transaction.commit();
        return  (String)pKey;
    }

    public void saveOrUpdate(OrderEntity entity) {
        getCurrentSession().saveOrUpdate(entity);
    }

    public void delete(String id) {
        Session session = null;
        session=getCurrentSession();
        Transaction transaction = session.beginTransaction();
        OrderEntity image = get(id);
        session.delete(image);
        transaction.commit();
    }

    public void flush() {
        getCurrentSession().flush();
    }
}