import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IShop, getShopIdentifier } from '../shop.model';

export type EntityResponseType = HttpResponse<IShop>;
export type EntityArrayResponseType = HttpResponse<IShop[]>;

@Injectable({ providedIn: 'root' })
export class ShopService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/shops');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(shop: IShop): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shop);
    return this.http
      .post<IShop>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(shop: IShop): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shop);
    return this.http
      .put<IShop>(`${this.resourceUrl}/${getShopIdentifier(shop) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(shop: IShop): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shop);
    return this.http
      .patch<IShop>(`${this.resourceUrl}/${getShopIdentifier(shop) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IShop>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IShop[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addShopToCollectionIfMissing(shopCollection: IShop[], ...shopsToCheck: (IShop | null | undefined)[]): IShop[] {
    const shops: IShop[] = shopsToCheck.filter(isPresent);
    if (shops.length > 0) {
      const shopCollectionIdentifiers = shopCollection.map(shopItem => getShopIdentifier(shopItem)!);
      const shopsToAdd = shops.filter(shopItem => {
        const shopIdentifier = getShopIdentifier(shopItem);
        if (shopIdentifier == null || shopCollectionIdentifiers.includes(shopIdentifier)) {
          return false;
        }
        shopCollectionIdentifiers.push(shopIdentifier);
        return true;
      });
      return [...shopsToAdd, ...shopCollection];
    }
    return shopCollection;
  }

  protected convertDateFromClient(shop: IShop): IShop {
    return Object.assign({}, shop, {
      closingHour: shop.closingHour?.isValid() ? shop.closingHour.toJSON() : undefined,
      openingHour: shop.openingHour?.isValid() ? shop.openingHour.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.closingHour = res.body.closingHour ? dayjs(res.body.closingHour) : undefined;
      res.body.openingHour = res.body.openingHour ? dayjs(res.body.openingHour) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((shop: IShop) => {
        shop.closingHour = shop.closingHour ? dayjs(shop.closingHour) : undefined;
        shop.openingHour = shop.openingHour ? dayjs(shop.openingHour) : undefined;
      });
    }
    return res;
  }
}
