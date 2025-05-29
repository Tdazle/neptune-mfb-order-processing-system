import {OrderService} from './order.service';
import type {OrderModel} from '../models/order.model';
import type {HttpClient} from '@angular/common/http';
import {of, throwError} from 'rxjs';

/**
 * Unit tests for the OrderService, verifying order retrieval, creation, error handling, and service instantiation.
 * Mocks HttpClient methods to simulate backend responses and error scenarios.
 */
describe('OrderService', () => {
  let httpClientMock: jasmine.SpyObj<HttpClient>;
  let service: OrderService;

  beforeEach(() => {
    httpClientMock = jasmine.createSpyObj('HttpClient', ['get', 'post']);
    service = new OrderService(httpClientMock);
  });

  /**
   * Tests that getOrders returns the expected list of orders and calls HttpClient. Get with the correct URL.
   *
   * Mocks the backend response and verifies the service retrieves and emits the correct order data.
   */
  it('shouldReturnListOfOrdersWhenGetOrdersIsCalled', (done) => {
    const mockOrders: OrderModel[] = [
      {id: 1, product: 'Widget', quantity: 2, status: 'pending'},
      {id: 2, product: 'Gadget', quantity: 1, status: 'shipped'}
    ];
    httpClientMock.get.and.returnValue(of(mockOrders));

    service.getOrders().subscribe((orders) => {
      expect(orders).toEqual(mockOrders);
      expect(httpClientMock.get).toHaveBeenCalledWith('http://localhost:8080/orders');
      done();
    });
  });

  /**
   * Verifies that getOrders returns the expected list of orders and calls HttpClient. Get with the correct URL.
   * Mocks the backend response and checks that the service emits the correct order data.
   */
  it('shouldCreateOrderAndReturnCreatedOrder', (done) => {
    const newOrder: OrderModel = {product: 'Widget', quantity: 3};
    const createdOrder: OrderModel = {id: 10, product: 'Widget', quantity: 3, status: 'pending'};
    httpClientMock.post.and.returnValue(of(createdOrder));

    service.createOrder(newOrder).subscribe((order) => {
      expect(order).toEqual(createdOrder);
      expect(httpClientMock.post).toHaveBeenCalledWith('http://localhost:8080/orders', newOrder);
      done();
    });
  });

  /**
   * Tests that getOrders returns the expected list of orders and verifies HttpClient. Get called with the correct URL.
   * Mocks the backend response and checks that the emitted orders match the mock data.
   */
  it('shouldInstantiateOrderServiceWithHttpClient', () => {
    expect(service).toBeInstanceOf(OrderService);
    // @ts-expect-error: private property
    expect(service.http).toBe(httpClientMock);
  });

  /**
   * Tests that getOrders returns the correct list of orders and verifies HttpClient. Get is called with the expected URL.
   * Mocks the backend response and checks that the emitted orders match the mock data.
   *
   * @param done Callback to signal asynchronous test completion.
   */
  it('shouldHandleErrorWhenGetOrdersFails', (done) => {
    const errorResponse = {status: 500, message: 'Internal Server Error'};
    httpClientMock.get.and.returnValue(throwError(() => errorResponse));

    service.getOrders().subscribe({
      /**
       * Error handler for successful responses when an error is expected.
       */
      next: () => {
        new Error('Expected error, but got success');
        done();
      },
      /**
       * Handles errors that occur during the retrieval of orders.
       * Verifies that the error received matches the mocked error response.
       * Signals test completion by calling the `done` callback.
       *
       * @param err - The error received from the HTTP request.
       */
      error: (err) => {
        expect(err).toEqual(errorResponse);
        done();
      }
    });
  });

  /**
   * Tests that getOrders returns the expected list of orders and calls HttpClient. Get with the correct URL.
   * Mocks the backend response and verifies the service emitted the correct order data.
   *
   * @param done Callback to signal asynchronous test completion.
   */
  it('shouldHandleInvalidOrderDataOnCreateOrder', (done) => {
    const invalidOrder: any = {quantity: 2}; // missing 'product'
    const errorResponse = {status: 400, message: 'Invalid order data'};
    httpClientMock.post.and.returnValue(throwError(() => errorResponse));

    service.createOrder(invalidOrder).subscribe({
      /**
       * Handles the unexpected success response when an error is expected.
       * Throws an error to indicate the test has failed and signals
       * asynchronous test completion.
       */
      next: () => {
        new Error('Expected error, but got success');
        done();
      },
      /**
       * Verifies that `createOrder` propagates the correct error when the backend responds with an error.
       * @param err - The error object emitted by `createOrder`.
       */
      error: (err) => {
        expect(err).toEqual(errorResponse);
        done();
      }
    });
  });

  /**
   * Tests that getOrders returns the expected list of orders and calls HttpClient. Get with the correct URL.
   * Mocks the backend response and verifies the service emitted the correct order data.
   *
   * @param done Callback to signal asynchronous test completion.
   */
  it('shouldHandleNetworkFailureOnHttpRequests', (done) => {
    const networkError = {status: 0, message: 'Network Error'};
    httpClientMock.get.and.returnValue(throwError(() => networkError));

    service.getOrders().subscribe({
      /**
       * Placeholder for handling successful responses.
       * This block should not be executed as the expectation is to receive a network error.
       * If executed, an error is thrown indicating an unexpected success.
       */
      next: () => {
        new Error('Expected network error, but got success');
        done();
      },
      /**
       * Handles errors that occur during HTTP requests.
       * Expects the error to match the `networkError` object and signals completion of the test.
       *
       * @param err - The error object received from the HTTP request.
       */
      error: (err) => {
        expect(err).toEqual(networkError);
        done();
      }
    });
  });
});
