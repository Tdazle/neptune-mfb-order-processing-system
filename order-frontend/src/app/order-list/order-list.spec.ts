import { OrderList } from './order-list';
import type { OrderModel } from '../models/order.model';
import type { OrderService } from '../services/order.service';
import {of, throwError, Subject, Observable} from 'rxjs';

describe('OrderList', () => {
  let orderServiceMock: jasmine.SpyObj<OrderService>;
  let component: OrderList;

  beforeEach(() => {
    orderServiceMock = jasmine.createSpyObj('OrderService', ['getOrders']);
    component = new OrderList(orderServiceMock);
  });

  /**
   * Verifies that orders are fetched and displayed when the component initializes.
   * Ensures the loading indicator is hidden and no error is present after a successful fetch.
   */
  it('shouldFetchAndDisplayOrdersOnInit', () => {
    const orders: OrderModel[] = [
      { id: 1, product: 'Widget', quantity: 2, status: 'shipped' },
      { id: 2, product: 'Gadget', quantity: 1, status: 'pending' }
    ];
    orderServiceMock.getOrders.and.returnValue(of(orders));
    component.ngOnInit();
    expect(orderServiceMock.getOrders).toHaveBeenCalled();
    expect(component.orders).toEqual(orders);
    expect(component.loading).toBe(false);
    expect(component.error).toBeNull();
  });

  /**
   * Tests that orders are fetched and displayed when the component initializes.
   * Verifies that the loading indicator is hidden and no error is present after a successful fetch.
   */
  it('shouldShowLoadingIndicatorWhileFetchingOrders', () => {
    const subject = new Subject<OrderModel[]>();
    orderServiceMock.getOrders.and.returnValue(subject.asObservable());
    component.fetchOrders();
    expect(component.loading).toBe(true);
    subject.next([{ id: 1, product: 'Widget', quantity: 2 }]);
    subject.complete();
    expect(component.loading).toBe(false);
  });

  /**
   * Tests that orders are fetched and displayed when the component initializes.
   * Verifies that the loading indicator is hidden and no error is present after a successful fetch.
   */
  it('shouldRefreshOrdersWhenFetchOrdersIsCalled', () => {
    const firstOrders: OrderModel[] = [{ id: 1, product: 'Widget', quantity: 2 }];
    const secondOrders: OrderModel[] = [{ id: 2, product: 'Gadget', quantity: 1 }];
    // Return firstOrders on first call, secondOrders on second call
    orderServiceMock.getOrders.and.returnValues(of(firstOrders), of(secondOrders));
    component.fetchOrders();
    expect(component.orders).toEqual(firstOrders);
    component.fetchOrders();
    expect(component.orders).toEqual(secondOrders);
  });


  /**
   * Tests that orders are fetched and displayed when the component initializes.
   * Verifies that the loading indicator is hidden and no error is present after a successful fetch.
   */
  it('shouldDisplayErrorMessageOnFetchFailure', () => {
    orderServiceMock.getOrders.and.returnValue(throwError(() => new Error('Network error')));
    component.fetchOrders();
    expect(component.error).toBe('Failed to load orders');
    expect(component.loading).toBe(false);
  });

  /**
   * Tests that orders are fetched and displayed on component initialization.
   * Verifies that the loading indicator is hidden and no error is present after successful fetch.
   */
  it('shouldHandleEmptyOrderListResponse', () => {
    orderServiceMock.getOrders.and.returnValue(of([]));
    component.fetchOrders();
    expect(component.orders).toEqual([]);
    expect(component.error).toBeNull();
    expect(component.loading).toBe(false);
  });

  /**
   * Tests that orders are fetched and displayed correctly on component initialization.
   * Verifies that the loading indicator is hidden and no error is present after successful fetch.
   */
  it('shouldResetErrorStateOnNewFetchOrdersCall', () => {
    // First, simulate a failed fetch to set error
    orderServiceMock.getOrders.and.returnValue(throwError(() => new Error('fail')));
    component.fetchOrders();
    expect(component.error).toBe('Failed to load orders');
    // Then, simulate a successful fetch and check error is cleared
    orderServiceMock.getOrders.and.returnValue(of([{ id: 1, product: 'Widget', quantity: 2 }]));
    component.fetchOrders();
    expect(component.error).toBeNull();
  });
});
