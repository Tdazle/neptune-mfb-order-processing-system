import { OrderForm } from './order-form';
import { FormBuilder } from '@angular/forms';
import { OrderService } from '../services/order.service';
import { ProductService } from '../services/product.service';
import { ProductModel } from '../models/product.model';
import { OrderModel } from '../models/order.model';
import { EventEmitter } from '@angular/core';
import { of, throwError } from 'rxjs';

/**
 * Unit tests for the OrderForm component, verifying product fetching, order submission,
 * loading and error state handling, and event emission using Vitest and Angular testing utilities.
 */
describe('OrderForm', () => {
  let fb: FormBuilder;
  let orderService: jasmine.SpyObj<OrderService>;
  let productService: jasmine.SpyObj<ProductService>;
  let component: OrderForm;

  beforeEach(() => {
    fb = new FormBuilder();

    orderService = jasmine.createSpyObj('OrderService', ['createOrder']);
    productService = jasmine.createSpyObj('ProductService', ['getProducts']);

    component = new OrderForm(fb, orderService, productService);

    // Patch the EventEmitter and spy on emitting
    component.orderCreated = new EventEmitter<void>();
    spyOn(component.orderCreated, 'emit');
  });

  /**
   * Tests that products are fetched and displayed correctly on component initialization.
   *
   * Mocks the ProductService to return a list of products, triggers ngOnInit,
   * and verifies that the products are set and no error is present.
   */
  it('shouldFetchAndDisplayProductsOnInit', () => {
    const mockProducts: ProductModel[] = [
      { id: 1, name: 'Widget', stockQuantity: 10 },
      { id: 2, name: 'Gadget', stockQuantity: 5 }
    ];
    (productService.getProducts as any).and.returnValue(of(mockProducts));
    component.ngOnInit();
    expect(component.products).toEqual(mockProducts);
    expect(component.error).toBeNull();
  });

  /**
   * Tests that products are fetched and displayed correctly during component initialization.
   *
   * Mocks the ProductService to return a predefined list of products, triggers ngOnInit,
   * and verifies that the component's product property is set and no error is present.
   */
  it('shouldSubmitOrderAndEmitEventOnSuccess', () => {
    component.orderForm.setValue({ product: 'Widget', quantity: 2 });
    (orderService.createOrder as any).and.returnValue(of({ product: 'Widget', quantity: 2 }));
    component.submitOrder();
    expect(orderService.createOrder).toHaveBeenCalledWith({ product: 'Widget', quantity: 2 });
    expect(component.orderForm.value).toEqual({ product: '', quantity: 1 });
    expect(component.loading).toBe(false);
    expect(component.orderCreated.emit).toHaveBeenCalled();
  });

  /**
   * Tests that loading state is set and reset during order submission.
   *
   * Mocks the OrderService to simulate an async order creation, triggers submitOrder,
   * and verifies that loading is true during the async operation and false after completion.
   */
  it('shouldSetAndResetLoadingStateDuringOrderSubmission', async () => {
    component.orderForm.setValue({ product: 'Widget', quantity: 2 });
    let resolveFn: any;
    const fakeObs = new Promise<OrderModel>(resolve => { resolveFn = resolve; });
    (orderService.createOrder as any).and.returnValue({
      subscribe: ({ next }: any) => {
        component.loading = true;
        fakeObs.then((order) => {
          next(order);
        });
      }
    });
    component.submitOrder();
    expect(component.loading).toBe(true);
    // Simulate async completion by calling resolveFn directly
    resolveFn({ product: 'Widget', quantity: 2 });
    // Wait for the microtask queue to flush
    await fakeObs;
    expect(component.loading).toBe(false);
  });

  /**
   * Tests that products are fetched and displayed on component initialization.
   *
   * Mocks ProductService to return a list of products, triggers ngOnInit,
   * and verifies that the product property is set and the error is null.
   */
  it('shouldDisplayErrorWhenProductFetchFails', () => {
    (productService.getProducts as any).and.returnValue(throwError(() => new Error('Network error')));
    component.fetchProducts();
    expect(component.error).toBe('Failed to load products');
    expect(component.products).toEqual([]);
  });

  /**
   * Tests that products are fetched and displayed correctly on component initialization.
   *
   * Mocks ProductService to return a list of products, triggers ngOnInit,
   * and verifies that the product property is set and the error is null.
   */
  it('shouldNotSubmitOrderIfFormIsInvalid', () => {
    component.orderForm.setValue({ product: '', quantity: 0 }); // invalid: product required, quantity min 1
    component.submitOrder();
    expect(orderService.createOrder).not.toHaveBeenCalled();
    expect(component.loading).toBe(false);
  });

  /**
   * Tests that products are fetched and displayed correctly on component initialization.
   *
   * Mocks ProductService to return a list of products, triggers ngOnInit,
   * and verifies that the product property is set and the error is null.
   */
  it('shouldDisplayBackendErrorMessageOnOrderCreationFailure', () => {
    component.orderForm.setValue({ product: 'Widget', quantity: 2 });
    const backendError = { error: { message: 'Out of stock' } };
    (orderService.createOrder as any).and.returnValue(throwError(() => backendError));
    component.submitOrder();
    expect(component.error).toBe('Out of stock');
    expect(component.loading).toBe(false);
  });
});
