import { IUser } from 'app/entities/user/user.model';
import { IOrder } from 'app/entities/order/order.model';
import { IPayment } from 'app/entities/payment/payment.model';

export interface IClient {
  id?: number;
  balance?: number | null;
  orderCount?: number | null;
  user?: IUser | null;
  deliveries?: IOrder[] | null;
  payments?: IPayment[] | null;
}

export class Client implements IClient {
  constructor(
    public id?: number,
    public balance?: number | null,
    public orderCount?: number | null,
    public user?: IUser | null,
    public deliveries?: IOrder[] | null,
    public payments?: IPayment[] | null
  ) {}
}

export function getClientIdentifier(client: IClient): number | undefined {
  return client.id;
}
