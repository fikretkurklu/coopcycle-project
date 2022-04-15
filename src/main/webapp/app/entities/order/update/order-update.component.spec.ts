import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrderService } from '../service/order.service';
import { IOrder, Order } from '../order.model';
import { IDeliveryMan } from 'app/entities/delivery-man/delivery-man.model';
import { DeliveryManService } from 'app/entities/delivery-man/service/delivery-man.service';
import { IShop } from 'app/entities/shop/shop.model';
import { ShopService } from 'app/entities/shop/service/shop.service';
import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';

import { OrderUpdateComponent } from './order-update.component';

describe('Order Management Update Component', () => {
  let comp: OrderUpdateComponent;
  let fixture: ComponentFixture<OrderUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orderService: OrderService;
  let deliveryManService: DeliveryManService;
  let shopService: ShopService;
  let clientService: ClientService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrderUpdateComponent],
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
      .overrideTemplate(OrderUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orderService = TestBed.inject(OrderService);
    deliveryManService = TestBed.inject(DeliveryManService);
    shopService = TestBed.inject(ShopService);
    clientService = TestBed.inject(ClientService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call DeliveryMan query and add missing value', () => {
      const order: IOrder = { id: 456 };
      const deliveryMan: IDeliveryMan = { id: 24182 };
      order.deliveryMan = deliveryMan;

      const deliveryManCollection: IDeliveryMan[] = [{ id: 75747 }];
      jest.spyOn(deliveryManService, 'query').mockReturnValue(of(new HttpResponse({ body: deliveryManCollection })));
      const additionalDeliveryMen = [deliveryMan];
      const expectedCollection: IDeliveryMan[] = [...additionalDeliveryMen, ...deliveryManCollection];
      jest.spyOn(deliveryManService, 'addDeliveryManToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ order });
      comp.ngOnInit();

      expect(deliveryManService.query).toHaveBeenCalled();
      expect(deliveryManService.addDeliveryManToCollectionIfMissing).toHaveBeenCalledWith(deliveryManCollection, ...additionalDeliveryMen);
      expect(comp.deliveryMenSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Shop query and add missing value', () => {
      const order: IOrder = { id: 456 };
      const shop: IShop = { id: 48160 };
      order.shop = shop;

      const shopCollection: IShop[] = [{ id: 369 }];
      jest.spyOn(shopService, 'query').mockReturnValue(of(new HttpResponse({ body: shopCollection })));
      const additionalShops = [shop];
      const expectedCollection: IShop[] = [...additionalShops, ...shopCollection];
      jest.spyOn(shopService, 'addShopToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ order });
      comp.ngOnInit();

      expect(shopService.query).toHaveBeenCalled();
      expect(shopService.addShopToCollectionIfMissing).toHaveBeenCalledWith(shopCollection, ...additionalShops);
      expect(comp.shopsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Client query and add missing value', () => {
      const order: IOrder = { id: 456 };
      const client: IClient = { id: 95691 };
      order.client = client;

      const clientCollection: IClient[] = [{ id: 87981 }];
      jest.spyOn(clientService, 'query').mockReturnValue(of(new HttpResponse({ body: clientCollection })));
      const additionalClients = [client];
      const expectedCollection: IClient[] = [...additionalClients, ...clientCollection];
      jest.spyOn(clientService, 'addClientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ order });
      comp.ngOnInit();

      expect(clientService.query).toHaveBeenCalled();
      expect(clientService.addClientToCollectionIfMissing).toHaveBeenCalledWith(clientCollection, ...additionalClients);
      expect(comp.clientsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const order: IOrder = { id: 456 };
      const deliveryMan: IDeliveryMan = { id: 48686 };
      order.deliveryMan = deliveryMan;
      const shop: IShop = { id: 70853 };
      order.shop = shop;
      const client: IClient = { id: 8912 };
      order.client = client;

      activatedRoute.data = of({ order });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(order));
      expect(comp.deliveryMenSharedCollection).toContain(deliveryMan);
      expect(comp.shopsSharedCollection).toContain(shop);
      expect(comp.clientsSharedCollection).toContain(client);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Order>>();
      const order = { id: 123 };
      jest.spyOn(orderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ order });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: order }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(orderService.update).toHaveBeenCalledWith(order);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Order>>();
      const order = new Order();
      jest.spyOn(orderService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ order });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: order }));
      saveSubject.complete();

      // THEN
      expect(orderService.create).toHaveBeenCalledWith(order);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Order>>();
      const order = { id: 123 };
      jest.spyOn(orderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ order });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orderService.update).toHaveBeenCalledWith(order);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackDeliveryManById', () => {
      it('Should return tracked DeliveryMan primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackDeliveryManById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackShopById', () => {
      it('Should return tracked Shop primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackShopById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackClientById', () => {
      it('Should return tracked Client primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackClientById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
