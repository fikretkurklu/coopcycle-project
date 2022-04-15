import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ShopService } from '../service/shop.service';
import { IShop, Shop } from '../shop.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ICooperative } from 'app/entities/cooperative/cooperative.model';
import { CooperativeService } from 'app/entities/cooperative/service/cooperative.service';

import { ShopUpdateComponent } from './shop-update.component';

describe('Shop Management Update Component', () => {
  let comp: ShopUpdateComponent;
  let fixture: ComponentFixture<ShopUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let shopService: ShopService;
  let userService: UserService;
  let cooperativeService: CooperativeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ShopUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ShopUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ShopUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    shopService = TestBed.inject(ShopService);
    userService = TestBed.inject(UserService);
    cooperativeService = TestBed.inject(CooperativeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const shop: IShop = { id: 456 };
      const user: IUser = { id: 52832 };
      shop.user = user;

      const userCollection: IUser[] = [{ id: 1962 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ shop });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Cooperative query and add missing value', () => {
      const shop: IShop = { id: 456 };
      const cooperative: ICooperative = { id: 35489 };
      shop.cooperative = cooperative;

      const cooperativeCollection: ICooperative[] = [{ id: 41382 }];
      jest.spyOn(cooperativeService, 'query').mockReturnValue(of(new HttpResponse({ body: cooperativeCollection })));
      const additionalCooperatives = [cooperative];
      const expectedCollection: ICooperative[] = [...additionalCooperatives, ...cooperativeCollection];
      jest.spyOn(cooperativeService, 'addCooperativeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ shop });
      comp.ngOnInit();

      expect(cooperativeService.query).toHaveBeenCalled();
      expect(cooperativeService.addCooperativeToCollectionIfMissing).toHaveBeenCalledWith(cooperativeCollection, ...additionalCooperatives);
      expect(comp.cooperativesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const shop: IShop = { id: 456 };
      const user: IUser = { id: 37255 };
      shop.user = user;
      const cooperative: ICooperative = { id: 10077 };
      shop.cooperative = cooperative;

      activatedRoute.data = of({ shop });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(shop));
      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.cooperativesSharedCollection).toContain(cooperative);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Shop>>();
      const shop = { id: 123 };
      jest.spyOn(shopService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shop });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: shop }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(shopService.update).toHaveBeenCalledWith(shop);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Shop>>();
      const shop = new Shop();
      jest.spyOn(shopService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shop });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: shop }));
      saveSubject.complete();

      // THEN
      expect(shopService.create).toHaveBeenCalledWith(shop);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Shop>>();
      const shop = { id: 123 };
      jest.spyOn(shopService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shop });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(shopService.update).toHaveBeenCalledWith(shop);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserById', () => {
      it('Should return tracked User primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackCooperativeById', () => {
      it('Should return tracked Cooperative primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackCooperativeById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
