import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IOrder, Order } from '../order.model';
import { OrderService } from '../service/order.service';
import { IDeliveryMan } from 'app/entities/delivery-man/delivery-man.model';
import { DeliveryManService } from 'app/entities/delivery-man/service/delivery-man.service';
import { IShop } from 'app/entities/shop/shop.model';
import { ShopService } from 'app/entities/shop/service/shop.service';
import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';

@Component({
  selector: 'jhi-order-update',
  templateUrl: './order-update.component.html',
})
export class OrderUpdateComponent implements OnInit {
  isSaving = false;

  deliveryMenSharedCollection: IDeliveryMan[] = [];
  shopsSharedCollection: IShop[] = [];
  clientsSharedCollection: IClient[] = [];

  editForm = this.fb.group({
    id: [],
    deliveryAddress: [],
    takeoutAdress: [],
    deliveryMan: [],
    shop: [],
    client: [],
  });

  constructor(
    protected orderService: OrderService,
    protected deliveryManService: DeliveryManService,
    protected shopService: ShopService,
    protected clientService: ClientService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ order }) => {
      this.updateForm(order);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const order = this.createFromForm();
    if (order.id !== undefined) {
      this.subscribeToSaveResponse(this.orderService.update(order));
    } else {
      this.subscribeToSaveResponse(this.orderService.create(order));
    }
  }

  trackDeliveryManById(_index: number, item: IDeliveryMan): number {
    return item.id!;
  }

  trackShopById(_index: number, item: IShop): number {
    return item.id!;
  }

  trackClientById(_index: number, item: IClient): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrder>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(order: IOrder): void {
    this.editForm.patchValue({
      id: order.id,
      deliveryAddress: order.deliveryAddress,
      takeoutAdress: order.takeoutAdress,
      deliveryMan: order.deliveryMan,
      shop: order.shop,
      client: order.client,
    });

    this.deliveryMenSharedCollection = this.deliveryManService.addDeliveryManToCollectionIfMissing(
      this.deliveryMenSharedCollection,
      order.deliveryMan
    );
    this.shopsSharedCollection = this.shopService.addShopToCollectionIfMissing(this.shopsSharedCollection, order.shop);
    this.clientsSharedCollection = this.clientService.addClientToCollectionIfMissing(this.clientsSharedCollection, order.client);
  }

  protected loadRelationshipsOptions(): void {
    this.deliveryManService
      .query()
      .pipe(map((res: HttpResponse<IDeliveryMan[]>) => res.body ?? []))
      .pipe(
        map((deliveryMen: IDeliveryMan[]) =>
          this.deliveryManService.addDeliveryManToCollectionIfMissing(deliveryMen, this.editForm.get('deliveryMan')!.value)
        )
      )
      .subscribe((deliveryMen: IDeliveryMan[]) => (this.deliveryMenSharedCollection = deliveryMen));

    this.shopService
      .query()
      .pipe(map((res: HttpResponse<IShop[]>) => res.body ?? []))
      .pipe(map((shops: IShop[]) => this.shopService.addShopToCollectionIfMissing(shops, this.editForm.get('shop')!.value)))
      .subscribe((shops: IShop[]) => (this.shopsSharedCollection = shops));

    this.clientService
      .query()
      .pipe(map((res: HttpResponse<IClient[]>) => res.body ?? []))
      .pipe(map((clients: IClient[]) => this.clientService.addClientToCollectionIfMissing(clients, this.editForm.get('client')!.value)))
      .subscribe((clients: IClient[]) => (this.clientsSharedCollection = clients));
  }

  protected createFromForm(): IOrder {
    return {
      ...new Order(),
      id: this.editForm.get(['id'])!.value,
      deliveryAddress: this.editForm.get(['deliveryAddress'])!.value,
      takeoutAdress: this.editForm.get(['takeoutAdress'])!.value,
      deliveryMan: this.editForm.get(['deliveryMan'])!.value,
      shop: this.editForm.get(['shop'])!.value,
      client: this.editForm.get(['client'])!.value,
    };
  }
}
