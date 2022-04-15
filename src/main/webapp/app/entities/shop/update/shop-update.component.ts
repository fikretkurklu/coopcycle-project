import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IShop, Shop } from '../shop.model';
import { ShopService } from '../service/shop.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ICooperative } from 'app/entities/cooperative/cooperative.model';
import { CooperativeService } from 'app/entities/cooperative/service/cooperative.service';

@Component({
  selector: 'jhi-shop-update',
  templateUrl: './shop-update.component.html',
})
export class ShopUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  cooperativesSharedCollection: ICooperative[] = [];

  editForm = this.fb.group({
    id: [],
    rating: [],
    open: [],
    averageDeliveryTime: [],
    closingHour: [],
    openingHour: [],
    tags: [],
    user: [],
    cooperative: [],
  });

  constructor(
    protected shopService: ShopService,
    protected userService: UserService,
    protected cooperativeService: CooperativeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ shop }) => {
      if (shop.id === undefined) {
        const today = dayjs().startOf('day');
        shop.closingHour = today;
        shop.openingHour = today;
      }

      this.updateForm(shop);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const shop = this.createFromForm();
    if (shop.id !== undefined) {
      this.subscribeToSaveResponse(this.shopService.update(shop));
    } else {
      this.subscribeToSaveResponse(this.shopService.create(shop));
    }
  }

  trackUserById(_index: number, item: IUser): number {
    return item.id!;
  }

  trackCooperativeById(_index: number, item: ICooperative): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IShop>>): void {
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

  protected updateForm(shop: IShop): void {
    this.editForm.patchValue({
      id: shop.id,
      rating: shop.rating,
      open: shop.open,
      averageDeliveryTime: shop.averageDeliveryTime,
      closingHour: shop.closingHour ? shop.closingHour.format(DATE_TIME_FORMAT) : null,
      openingHour: shop.openingHour ? shop.openingHour.format(DATE_TIME_FORMAT) : null,
      tags: shop.tags,
      user: shop.user,
      cooperative: shop.cooperative,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, shop.user);
    this.cooperativesSharedCollection = this.cooperativeService.addCooperativeToCollectionIfMissing(
      this.cooperativesSharedCollection,
      shop.cooperative
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.cooperativeService
      .query()
      .pipe(map((res: HttpResponse<ICooperative[]>) => res.body ?? []))
      .pipe(
        map((cooperatives: ICooperative[]) =>
          this.cooperativeService.addCooperativeToCollectionIfMissing(cooperatives, this.editForm.get('cooperative')!.value)
        )
      )
      .subscribe((cooperatives: ICooperative[]) => (this.cooperativesSharedCollection = cooperatives));
  }

  protected createFromForm(): IShop {
    return {
      ...new Shop(),
      id: this.editForm.get(['id'])!.value,
      rating: this.editForm.get(['rating'])!.value,
      open: this.editForm.get(['open'])!.value,
      averageDeliveryTime: this.editForm.get(['averageDeliveryTime'])!.value,
      closingHour: this.editForm.get(['closingHour'])!.value
        ? dayjs(this.editForm.get(['closingHour'])!.value, DATE_TIME_FORMAT)
        : undefined,
      openingHour: this.editForm.get(['openingHour'])!.value
        ? dayjs(this.editForm.get(['openingHour'])!.value, DATE_TIME_FORMAT)
        : undefined,
      tags: this.editForm.get(['tags'])!.value,
      user: this.editForm.get(['user'])!.value,
      cooperative: this.editForm.get(['cooperative'])!.value,
    };
  }
}
