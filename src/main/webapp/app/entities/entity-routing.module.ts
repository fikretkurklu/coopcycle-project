import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'order',
        data: { pageTitle: 'coopcyleJhipsterApp.order.home.title' },
        loadChildren: () => import('./order/order.module').then(m => m.OrderModule),
      },
      {
        path: 'client',
        data: { pageTitle: 'coopcyleJhipsterApp.client.home.title' },
        loadChildren: () => import('./client/client.module').then(m => m.ClientModule),
      },
      {
        path: 'delivery-man',
        data: { pageTitle: 'coopcyleJhipsterApp.deliveryMan.home.title' },
        loadChildren: () => import('./delivery-man/delivery-man.module').then(m => m.DeliveryManModule),
      },
      {
        path: 'payment',
        data: { pageTitle: 'coopcyleJhipsterApp.payment.home.title' },
        loadChildren: () => import('./payment/payment.module').then(m => m.PaymentModule),
      },
      {
        path: 'shop',
        data: { pageTitle: 'coopcyleJhipsterApp.shop.home.title' },
        loadChildren: () => import('./shop/shop.module').then(m => m.ShopModule),
      },
      {
        path: 'cooperative',
        data: { pageTitle: 'coopcyleJhipsterApp.cooperative.home.title' },
        loadChildren: () => import('./cooperative/cooperative.module').then(m => m.CooperativeModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
