import { IUser } from 'app/entities/user/user.model';
import { IOrder } from 'app/entities/order/order.model';

export interface IDeliveryMan {
  id?: number;
  vehicleType?: string | null;
  rides?: number | null;
  earned?: number | null;
  rating?: string | null;
  user?: IUser | null;
  deliveries?: IOrder[] | null;
}

export class DeliveryMan implements IDeliveryMan {
  constructor(
    public id?: number,
    public vehicleType?: string | null,
    public rides?: number | null,
    public earned?: number | null,
    public rating?: string | null,
    public user?: IUser | null,
    public deliveries?: IOrder[] | null
  ) {}
}

export function getDeliveryManIdentifier(deliveryMan: IDeliveryMan): number | undefined {
  return deliveryMan.id;
}
