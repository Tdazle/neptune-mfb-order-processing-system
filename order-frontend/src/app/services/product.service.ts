import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ProductModel} from '../models/product.model';

@Injectable({
  providedIn: 'root'
})

export class ProductService {
  private apiUrl = 'http://localhost:8081';

  /**
   * Constructs the ProductService and initializes the HttpClient for making HTTP requests.
   * @param http - The HttpClient used to perform HTTP requests.
   */
  constructor(private http: HttpClient) {}

  /**
   * Retrieves a list of products from the inventory.
   *
   * @returns An Observable of an array of ProductModel objects.
   */
  getProducts(): Observable<ProductModel[]> {
    return this.http.get<ProductModel[]>(`${this.apiUrl}/inventory/products`);
  }
}
