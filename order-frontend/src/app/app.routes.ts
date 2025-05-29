import { Routes } from '@angular/router';
import { OrderForm } from './order-form/order-form';
import { OrderList } from './order-list/order-list';

export const routes: Routes = [
  { path: '', redirectTo: 'orders', pathMatch: 'full' },
  { path: 'orders', component: OrderList },
  { path: 'create-order', component: OrderForm }
];
