import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IOrder } from 'app/entities/order/order.model';
import { IPayment } from 'app/entities/payment/payment.model';
import { ICooperative } from 'app/entities/cooperative/cooperative.model';

export interface IShop {
  id?: number;
  rating?: string | null;
  open?: boolean | null;
  averageDeliveryTime?: number | null;
  closingHour?: dayjs.Dayjs | null;
  openingHour?: dayjs.Dayjs | null;
  tags?: string | null;
  user?: IUser | null;
  deliveries?: IOrder[] | null;
  payments?: IPayment[] | null;
  cooperative?: ICooperative | null;
}

export class Shop implements IShop {
  constructor(
    public id?: number,
    public rating?: string | null,
    public open?: boolean | null,
    public averageDeliveryTime?: number | null,
    public closingHour?: dayjs.Dayjs | null,
    public openingHour?: dayjs.Dayjs | null,
    public tags?: string | null,
    public user?: IUser | null,
    public deliveries?: IOrder[] | null,
    public payments?: IPayment[] | null,
    public cooperative?: ICooperative | null
  ) {
    this.open = this.open ?? false;
  }
}

export function getShopIdentifier(shop: IShop): number | undefined {
  return shop.id;
}
