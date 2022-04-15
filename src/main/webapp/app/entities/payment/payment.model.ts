import { IShop } from 'app/entities/shop/shop.model';
import { IClient } from 'app/entities/client/client.model';

export interface IPayment {
  id?: number;
  paymentMethod?: string;
  amount?: number;
  shop?: IShop | null;
  client?: IClient | null;
}

export class Payment implements IPayment {
  constructor(
    public id?: number,
    public paymentMethod?: string,
    public amount?: number,
    public shop?: IShop | null,
    public client?: IClient | null
  ) {}
}

export function getPaymentIdentifier(payment: IPayment): number | undefined {
  return payment.id;
}
