package com.udacity.vehicles.api;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarNotFoundException;
import com.udacity.vehicles.service.CarService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;

import java.net.URI;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    @Autowired
    private CarResourceAssembler assembler;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        mvc.perform(
                        post(new URI("/cars"))
                                .content(json.write(car).getJson())
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Basic " + Base64Utils.encodeToString("admin:password".getBytes())))
                .andExpect(status().isCreated());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        /*
         * DONE: Add a test to check that the `get` method works by calling
         *   the whole list of vehicles. This should utilize the car from `getCar()`
         *   below (the vehicle will be the first in the list).
         */
        Car car = getCar();
        car.setId(1L);
        String expected = "{\"_embedded\":{\"carList\":[{\"id\":1,\"createdAt\":null,\"modifiedAt\":null,\"condition\":\"USED\",\"details\":{\"body\":\"sedan\",\"model\":\"Impala\",\"manufacturer\":{\"code\":101,\"name\":\"Chevrolet\"},\"numberOfDoors\":4,\"fuelType\":\"Gasoline\",\"engine\":\"3.6L V6\",\"mileage\":32280,\"modelYear\":2018,\"productionYear\":2018,\"externalColor\":\"white\"},\"location\":{\"lat\":40.73061,\"lon\":-73.935242,\"address\":null,\"city\":null,\"state\":null,\"zip\":null},\"price\":null,\"_links\":{\"self\":{\"href\":\"http://localhost/cars/1\"},\"cars\":{\"href\":\"http://localhost/cars\"}}}]},\"_links\":{\"self\":{\"href\":\"http://localhost/cars\"}}}";
        mvc.perform(get(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .header(HttpHeaders.AUTHORIZATION,
                                "Basic " + Base64Utils.encodeToString("admin:password".getBytes())))
                .andExpect(content().string(expected));
    }

    /**
     * Tests the read operation for a single car by ID.
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {
        /*
         * DONE: Add a test to check that the `get` method works by calling
         *   a vehicle by ID. This should utilize the car from `getCar()` below.
         */
        Car car = getCar();
        car.setId(1L);
        String expected = "{\"id\":1,\"createdAt\":null,\"modifiedAt\":null,\"condition\":\"USED\",\"details\":{\"body\":\"sedan\",\"model\":\"Impala\",\"manufacturer\":{\"code\":101,\"name\":\"Chevrolet\"},\"numberOfDoors\":4,\"fuelType\":\"Gasoline\",\"engine\":\"3.6L V6\",\"mileage\":32280,\"modelYear\":2018,\"productionYear\":2018,\"externalColor\":\"white\"},\"location\":{\"lat\":40.73061,\"lon\":-73.935242,\"address\":null,\"city\":null,\"state\":null,\"zip\":null},\"price\":null,\"_links\":{\"self\":{\"href\":\"http://localhost/cars/1\"},\"cars\":{\"href\":\"http://localhost/cars\"}}}";
        mvc.perform(get(new URI("/cars/1"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .header(HttpHeaders.AUTHORIZATION,
                                "Basic " + Base64Utils.encodeToString("admin:password".getBytes())))
                .andExpect(content().string(expected));
        given(carService.findById(any())).willThrow(new CarNotFoundException());
        mvc.perform(get(new URI("/cars/2"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .header(HttpHeaders.AUTHORIZATION,
                                "Basic " + Base64Utils.encodeToString("admin:password".getBytes())))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests the deletion of a single car by ID.
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        /*
         * DONE: Add a test to check whether a vehicle is appropriately deleted
         *   when the `delete` method is called from the Car Controller. This
         *   should utilize the car from `getCar()` below.
         */
        mvc.perform(delete(new URI("/cars/1"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .header(HttpHeaders.AUTHORIZATION,
                                "Basic " + Base64Utils.encodeToString("admin:password".getBytes())))
                .andExpect(status().isNoContent());
    }

    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }
}