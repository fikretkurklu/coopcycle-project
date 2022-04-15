import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPayment, Payment } from '../payment.model';
import { PaymentService } from '../service/payment.service';
import { IShop } from 'app/entities/shop/shop.model';
import { ShopService } from 'app/entities/shop/service/shop.service';
import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';

@Component({
  selector: 'jhi-payment-update',
  templateUrl: './payment-update.component.html',
})
export class PaymentUpdateComponent implements OnInit {
  isSaving = false;

  shopsSharedCollection: IShop[] = [];
  clientsSharedCollection: IClient[] = [];

  editForm = this.fb.group({
    id: [],
    paymentMethod: [null, [Validators.required]],
    amount: [null, [Validators.required]],
    shop: [],
    client: [],
  });

  constructor(
    protected paymentService: PaymentService,
    protected shopService: ShopService,
    protected clientService: ClientService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ payment }) => {
      this.updateForm(payment);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const payment = this.createFromForm();
    if (payment.id !== undefined) {
      this.subscribeToSaveResponse(this.paymentService.update(payment));
    } else {
      this.subscribeToSaveResponse(this.paymentService.create(payment));
    }
  }

  trackShopById(_index: number, item: IShop): number {
    return item.id!;
  }

  trackClientById(_index: number, item: IClient): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPayment>>): void {
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

  protected updateForm(payment: IPayment): void {
    this.editForm.patchValue({
      id: payment.id,
      paymentMethod: payment.paymentMethod,
      amount: payment.amount,
      shop: payment.shop,
      client: payment.client,
    });

    this.shopsSharedCollection = this.shopService.addShopToCollectionIfMissing(this.shopsSharedCollection, payment.shop);
    this.clientsSharedCollection = this.clientService.addClientToCollectionIfMissing(this.clientsSharedCollection, payment.client);
  }

  protected loadRelationshipsOptions(): void {
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

  protected createFromForm(): IPayment {
    return {
      ...new Payment(),
      id: this.editForm.get(['id'])!.value,
      paymentMethod: this.editForm.get(['paymentMethod'])!.value,
      amount: this.editForm.get(['amount'])!.value,
      shop: this.editForm.get(['shop'])!.value,
      client: this.editForm.get(['client'])!.value,
    };
  }
}
