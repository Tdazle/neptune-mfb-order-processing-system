import {ProductService} from './product.service';
import {HttpClient} from '@angular/common/http';
import {of, throwError} from 'rxjs';
import type {ProductModel} from '../models/product.model';

/**
 * Unit tests for the ProductService, verifying correct API endpoint usage,
 * successful retrieval of product lists, error propagation, and handling of
 * empty or malformed API responses. Uses Vitest for mocking and assertions.
 */
describe('ProductService', () => {
  let httpClientMock: jasmine.SpyObj<HttpClient>;
  let service: ProductService;

  beforeEach(() => {
    httpClientMock = jasmine.createSpyObj('HttpClient', ['get', 'post']);
    service = new ProductService(httpClientMock as unknown as HttpClient);
  });

  /**
   * Tests that getProducts() emits the expected product list when the API responds successfully.
   * Mocks the HTTP GET call to return a predefined array of ProductModel objects and verifies the output.
   * Calls done() on success or error to signal test completion.
   */
  it('shouldReturnProductListOnSuccessfulApiResponse', (done) => {
    const mockProducts: ProductModel[] = [
      {id: 1, name: 'Product A', stockQuantity: 10},
      {id: 2, name: 'Product B', stockQuantity: 5},
    ];
    httpClientMock.get.and.returnValue(of(mockProducts));

    service.getProducts().subscribe({
      next: (products) => {
        expect(products).toEqual(mockProducts);
        done();
      },
      error: done,
    });
  });

  /**
   * Verifies that getProducts() emits the expected product list when the API responds successfully.
   * Mocks the HTTP GET call to return a predefined array of ProductModel objects and checks the output.
   * Calls done() to signal test completion.
   */
  it('shouldCallCorrectApiEndpointWithGetMethod', () => {
    httpClientMock.get.and.returnValue(of([]));
    service.getProducts().subscribe();
    expect(httpClientMock.get).toHaveBeenCalledWith('http://localhost:8081/inventory/products');
  });

  /**
   * Tests that getProducts() emits the expected product list when the API responds successfully.
   * Mocks the HTTP GET call to return a predefined array of ProductModel objects and verifies the output.
   * Calls done() to signal test completion.
   */
  it('shouldEmitEmptyArrayWhenApiReturnsNoProducts', (done) => {
    httpClientMock.get.and.returnValue(of([]));
    service.getProducts().subscribe({
      next: (products) => {
        expect(products).toEqual([]);
        done();
      },
      error: done,
    });
  });

  /**
   * Tests that getProducts() emits the expected product list when the API responds successfully.
   * Mocks the HTTP GET call to return a predefined array of ProductModel objects and verifies the output.
   * Calls done() on success or error to signal test completion.
   */
  it('shouldPropagateErrorOnApiFailure', (done) => {
    const apiError = new Error('API failure');
    httpClientMock.get.and.returnValue(throwError(() => apiError));
    service.getProducts().subscribe({
      next: () => new Error('Expected error, got success') && done(),
      error: (err) => {
        expect(err).toBe(apiError);
        done();
      },
    });
  });

  /**
   * Tests that getProducts() emits the expected product list when the API responds successfully.
   * Mocks the HTTP GET call to return a predefined array of ProductModel objects and verifies the output.
   * Calls done() to signal test completion.
   */
  it('should return malformed data as is', (done) => {
    const malformedData = [{ foo: 'bar' }] as unknown as ProductModel[];
    httpClientMock.get.and.returnValue(of(malformedData as any));

    service.getProducts().subscribe({
      next: data => {
        expect(data).toEqual(malformedData);
        done();
      },
      error: done.fail
    });
  });


  /**
   * Tests that getProducts() emits the expected product list when the API responds successfully.
   * Mocks the HTTP GET call to return a predefined array of ProductModel objects and verifies the output.
   * Calls done() to signal test completion.
   */
  it('shouldEmitErrorWhenApiIsUnreachable', (done) => {
    const networkError = new Error('Network unreachable');
    httpClientMock.get.and.returnValue(throwError(() => networkError));
    service.getProducts().subscribe({
      next: () => new Error('Expected error, got success') && done(),
      error: (err) => {
        expect(err).toBe(networkError);
        done();
      },
    });
  });
});
