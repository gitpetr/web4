package DAO;

import interfaces.CarInterface;
import model.Car;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.DBHelper;

import java.sql.SQLException;
import java.util.List;

public class CarDao implements CarInterface {

    private SessionFactory sessionFactory;

    public CarDao() {
        this.sessionFactory = DBHelper.getSessionFactory();
    }

    @Override
    public Car buyCar(String brand, String model, String licensePlate) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Query<Car> query = session.createQuery("SELECT  c FROM Car c " +
                "where c.sold = false " +
                "and c.brand = :brand " +
                "and c.model = :model " +
                "and  c.licensePlate = :licensePlate", Car.class);
        query.setParameter("brand", brand);
        query.setParameter("model", model);
        query.setParameter("licensePlate", licensePlate);
        List<Car> list = query.list();
        if (list.isEmpty()) {
            return null;
        }

        Car car = list.get(0);
        car.setSold(true);
        updateCar(car);
        transaction.commit();
        session.close();
        return car;
    }

    @Override
    public Car findById(int id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Car car = session.get(Car.class, id);
        transaction.commit();
        session.close();
        return car;
    }

    @Override
    public void addCar(Car car) throws SQLException {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        int count = getAllCars().size();

        if (count > 9) {
            transaction.rollback();
            throw new SQLException("Auto not added");
        } else {
            session.save(car);
            transaction.commit();
        }

        session.close();
    }

    @Override
    public void updateCar(Car car) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.update(car);
        transaction.commit();
        session.close();
    }

    @Override
    public void deleteCar(Car car) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.delete(car);
        transaction.commit();
        session.close();
    }

    @Override
    public void deleteAllCars() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        List<Car> cars = (List<Car>) session.createQuery("From Car").list();
        cars.forEach(session::delete);
        transaction.commit();
        session.close();
    }

    public void deleteSoldcars() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        List<Car> cars = (List<Car>) session.createQuery("From Car c WHERE c.sold = true").list();
        cars.forEach(session::delete);
        transaction.commit();
        session.close();
    }

    @Override
    public List<Car> getAllCars() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        List<Car> cars = (List<Car>) session.createQuery("From Car c WHERE c.sold = false").list();
        transaction.commit();
        session.close();
        return cars;
    }

    public List<Car> getSoldCars() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        List<Car> cars = (List<Car>) session.createQuery("From Car c WHERE c.sold = true").list();
        transaction.commit();
        session.close();
        return cars;
    }
}
