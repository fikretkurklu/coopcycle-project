import { IShop } from 'app/entities/shop/shop.model';

export interface ICooperative {
  id?: number;
  city?: string;
  numberOfShop?: number | null;
  shops?: IShop[] | null;
}

export class Cooperative implements ICooperative {
  constructor(public id?: number, public city?: string, public numberOfShop?: number | null, public shops?: IShop[] | null) {}
}

export function getCooperativeIdentifier(cooperative: ICooperative): number | undefined {
  return cooperative.id;
}
