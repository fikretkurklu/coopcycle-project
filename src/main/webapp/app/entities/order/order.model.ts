import { IDeliveryMan } from 'app/entities/delivery-man/delivery-man.model';
import { IShop } from 'app/entities/shop/shop.model';
import { IClient } from 'app/entities/client/client.model';

export interface IOrder {
  id?: number;
  deliveryAddress?: string | null;
  takeoutAdress?: string | null;
  deliveryMan?: IDeliveryMan | null;
  shop?: IShop | null;
  client?: IClient | null;
}

export class Order implements IOrder {
  constructor(
    public id?: number,
    public deliveryAddress?: string | null,
    public takeoutAdress?: string | null,
    public deliveryMan?: IDeliveryMan | null,
    public shop?: IShop | null,
    public client?: IClient | null
  ) {}
}

export function getOrderIdentifier(order: IOrder): number | undefined {
  return order.id;
}
