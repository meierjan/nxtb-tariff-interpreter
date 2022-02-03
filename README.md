# Tariff Interpreter

This Interpreter is a reference implementation for computing a JSON formatted bike-sharing tariff model. The tariff
model is designed to be able to represent as many types of tariffs as possible and to have a modular structure. Thus, it
is possible to calculate as many tariffs as possible with a single tariff interpreter and at the same time ensure a
uniform format. In addition, a receipt is issued at the end thanks to a flexible slot system. In this way, the
composition of the travel costs is broken down transparently for the end customer.

## Contents

* [General Interpreter Operation](#general-interpreter-operation)
* [Overview JSON Schemas](#overview-json-schema)

## General Interpreter Operation

This section will be added when the interface is built.

## Overview JSON-Schema

The modular tariff model is designed in such a way that there are three basic types of tariffs. When creating a tariff,
you have to choose one. It is not possible to mix the tariffs. To decide which tariff is the right one, you can ask
yourself the following questions:

* _Is my tariff distance dependent?_

  Then this model is unfortunately not the right one for you, because only tariffs that are time based can be
  implemented.


* _Is my tariff exclusively dependent on the time driven and not on the time of day or the number of days driven?_

  Then your tariff is a [SlotBasedTariff](#slotbasedtariff).


* _Does my tariff depend not only on the duration of the trip, but also on the time of day (e.g. you pay more if you
  travel between eight and nine than if you travel between ten and eleven)?_

  Then your tariff is a [TimeBasedTariff](#timebasedtariff).

All tariffs are based on a modular slot system. The difference between the three tariff models is only the definition of
the slots and the calling of the slots. Each slot must be assigned a rate. The rate is used for the actual calculation
of the price. There are also different types of rates:

* The [FixedRate](#fixedrate) is the simplest form, with which one can specify a fixed amount, which is computed, as
  soon as this Slot is called.


* With the [TimeBasedRate](#timebasedrate) one can define an interval in which a fixed amount is calculated. With each
  repetition of this interval a fixed amount is added to it. The interval does not have to be completely finished, the
  amount is added when entering the interval.

### SlotBasedTariff

The SlotBasedTariff is designed to represent all common tariffs. It can be used to give a discount for long rentals.The
calculation model is very intuitive and easy to implement. A SlotBasedTariff is structured in such a way that the
duration of the trip is split into slots. The slots must start at zero and continue seamlessly, because all slots are
used until the end of the trip to calculate the final price. The price is calculated for each slot individually and then
added up.

###### Example 1

```JSON
{
  "type": "SlotBasedTariff",
  "id": 1,
  "currency": "EUR",
  "rates": [
    {
      "type": "FixedRate",
      "id": 2,
      "currency": "EUR",
      "price": {
        "credit": 100
      }
    },
    {
      "type": "TimeBasedRate",
      "id": 3,
      "currency": "EUR",
      "interval": {
        "timeAmount": 90,
        "timeUnit": "Minutes"
      },
      "pricePerInterval": {
        "credit": 100
      }
    }
  ],
  "slots": [
    {
      "rate": 2,
      "start": {
        "timeAmount": 0,
        "timeUnit": "MINUTES"
      },
      "end": {
        "timeAmount": 2,
        "timeUnit": "HOURS"
      }
    },
    {
      "rate": 3,
      "start": {
        "timeAmount": 2,
        "timeUnit": "HOURS"
      }
    }
  ]
}
```

This tariff has two slots with an associated rate. The first slot begins at the start of the trip and covers the first
two hours. It does not matter if the bike is rented for ten minutes or two hours. During this time, one euro is charged.
The second slot starts at two hours and is not limited. The TimeBasedRate belongs to this slot. If one rides now for
three hours, one euro from the second slot is added to the one euro from the first slot, because the interval was
started once. If one travels for five hours, three euros from the second slot are added to the one euro from the first
slot, because the interval was started three times.

##### BillingInterval

To make this structure more flexible, to have the opportunity to implement tariffs with a cyclic sequence of slots, it
is also possible to define a **billing-interval**. This will divide the time of the calculation-interval and repeat
applying the tariff calculation on each time window. This means that after the billing interval the calculation starts
again with the first slot. If there is a slot with a max-price it will be reset.

###### Example 2

```JSON
{
  "type": "SlotBasedTariff",
  "id": 1,
  "currency": "EUR",
  "billingInterval": {
    "timeAmount": 1,
    "timeUnit": "DAYS"
  },
  "rates": [
    {
      "type": "TimeBasedRate",
      "id": 2,
      "currency": "EUR",
      "interval": {
        "timeAmount": 1,
        "timeUnit": "HOURS"
      },
      "basePrice": {
        "credit": 0
      },
      "minPrice": {
        "credit": 0
      },
      "maxPrice": {
        "credit": 1500
      },
      "pricePerInterval": {
        "credit": 100
      }
    }
  ],
  "slots": [
    {
      "rate": 2,
      "start": {
        "timeAmount": 0,
        "timeUnit": "MINUTES"
      }
    }
  ]
}
```

Up to one day it is a normal calculation(every hour one euro is added. Maximal 15€). For a calculation-interval over one
day. The calculation will be reset(maxPrice will reset and calculation will restart at the first slot) and added up.
This means that for a trip of 30 hours, 15€ from the first duration of the billing-interval and six euro from the second
duration are added together. This is repeated with each billing-interval duration.

### TimeBasedTariff

The TimeBasedTariff is designed to implement specific tariffs depending on the day of the week and time of day. It can
be used to offer a lower price on weekends or at night. A TimeBasedTariff is also composed of individual slots. However,
the calculation does not have to start with the first slot, but with the slot with start time intersection. All slots
have a start time and an end time. These times always refer to a week and consist of a weekday and a time. The slots
together must cover a whole week. As with the SlotBasedTariff, a rate is also assigned to these slots. The calculation
works similarly to a SlotBasedTariff, except that the slots matching the time of day are picked out.

###### Example 3

```Json
{
  "type": "TimeBasedTariff",
  "id": 1,
  "currency": "EUR",
  "timeZone": "GMT+1",
  "rates": [
    {
      "type": "FixedRate",
      "id": 2,
      "currency": "EUR",
      "price": {
        "credit": 200
      }
    },
    {
      "type": "FixedRate",
      "id": 3,
      "currency": "EUR",
      "price": {
        "credit": 100
      }
    }
  ],
  "timeSlots": [
    {
      "rate": 2,
      "from": {
        "day": "FRIDAY",
        "hour": 16,
        "minutes": 0
      },
      "to": {
        "day": "MONDAY",
        "hour": 5,
        "minutes": 0
      }
    },
    {
      "rate": 3,
      "from": {
        "day": "MONDAY",
        "hour": 5,
        "minutes": 0
      },
      "to": {
        "day": "FRIDAY",
        "hour": 16,
        "minutes": 0
      }
    }
  ]
}
```

The tariff consists of two slots. The first one starts Friday 4pm and ends Monday 5am and has a FixedRate assigned,
which charges two Euros. The second slot starts Monday 5am and ends Friday 4pm and has a FixedRate assigned, which
charges one euro. A rental from Tuesday 8am to Saturday 8am will be charged three Euros, as both slots are cut. With a
rental period of exactly two weeks and a start time of Monday 10 am, both slots will be charged exactly twice and there
will be a cost of 6 euros.

#### FixedRate

| Field Name | Required |  Type  | Defines                                                                            |
|------------|:--------:|:------:|------------------------------------------------------------------------------------|
| type       |   Yes    | String | Necessary to identify the slot type. In this case 'FixedRate'                      |
| id         |   Yes    |  Long  | Unique identifier of this rate                                                     | 
| currency   |   Yes    | String | Currency used to pay the rate [(ISO 4217)](https://de.wikipedia.org/wiki/ISO_4217) |
| price      |   Yes    | Price  | Rate price                                                                         | 

###### Example

```Json
{
  "type": "FixedRate",
  "id": 1,
  "currency": "EUR",
  "price": {
    "credit": 300
  }
}
```

The duration in this slot can be ignored. When this slot is started, 3€ will be charged

#### TimeBasedRate

| Field Name       | Required |  Type  | Defines                                                                            |
|------------------|:--------:|:------:|------------------------------------------------------------------------------------|
| type             |   Yes    | String | Necessary to identify the slot type. In this case 'TimeBasedRate'                  |
| id               |   Yes    |  Long  | Unique identifier of this rate                                                     | 
| currency         |   Yes    | String | Currency used to pay the rate [(ISO 4217)](https://de.wikipedia.org/wiki/ISO_4217) |
| basePrice        | Optional | Price  | Price calculated per started slot                                                  |
| interval         |   Yes    | Price  | Interval in which the price of this rate will reapplied                            |
| pricePerInterval |   Yes    | Price  | Price calculated per interval                                                      | 
| maxPrice         | Optional | Price  | Maximal price for this rate                                                        |
| minPrice         | Optional | Price  | Minimal price for this rate                                                        |

###### Example

```Json
{
  "type": "TimeBasedRate",
  "id": 1,
  "currency": "EUR",
  "basePrice": {
    "credit": 200
  },
  "interval": {
    "timeAmount": 15,
    "timeUnit": "MINUTES"
  },
  "pricePerInterval": {
    "credit": 100
  },
  "maxPrice": {
    "credit": 1000
  },
  "minPrice": {
    "credit": 400
  }
}
```

* For the rental of 10 minutes in this slot, one will pay 4€ -> The calculated price is smaller than the minPrice (200
  basePrice + 1 x 100 pricePerInterval < 400 minPrice)
* For the rental of 38 minutes in this slot, one will pay 5€

  200 basePrice

  \+ two times a full interval + 1 started interval -> 3 x 100 = 500
* For the rental of 140min in this slot, one will pay 10€ -> The calculated price is bigger then the maxPrice (200
  basePrice + 10 x 100 pricePerInterval > 1000 maxPrice)

### Goodwill

To give a tariff a discount, one can add a Goodwill object to each type of tariff. There are three different types of
goodwill. All types are deducted from the rental period before the actual calculation and appear on the receipt.

There are the following types:

#### StaticGoodwill

Subtracts a static time from the rental duration from behind

```Json
{
  "type": "StaticGoodwill",
  "duration": {
    "timeAmount": 100,
    "timeUnit": "SECONDS"
  }
}
```

100 seconds are deducted

#### DynamicGoodwill

Subtracts a dynamic time from the rental duration from behind

 ```Json
{
  "type": "DynamicGoodwill",
  "deductibleProportionInPercentage": 10.0
}
```

10% of the rental period will be deducted

#### FreeMinutes

Subtracts a static time from the rental period from the start

```Json
{
  "type": "FreeMinutes",
  "duration": {
    "timeAmount": 10,
    "timeUnit": "MINUTES"
  }
}
```

10 minutes are deducted
