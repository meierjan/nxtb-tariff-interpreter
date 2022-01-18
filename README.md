# Tariff Interpreter

This Interpreter is a reference implementation for computing a JSON formatted bike-sharing tariff model. The tariff
model is designed to be able to represent as many types of tariffs as possible and to have a modular structure. Thus, it
is possible to calculate as many tariffs as possible with a single tariff interpreter and at the same time ensure a
uniform format. In addition, a kind of receipt is issued at the end thanks to a flexible slot system. Thus, the
composition of the travel costs is broken down as transparently as possible for the end customer.

## Contents

* [General Interpreter Operation](#general-interpreter-operation)
* [Overview JSON Schemas](#overview-json-schema)

## General Interpreter Operation

This section will be added when the interface is built.

## Overview JSON-Schema

The modular tariff model is designed in such a way that there are three basic types of tariffs. When creating a tariff,
you have to choose one. It is not possible to mix the tariffs. To decide which tariff is the right one, you can ask
yourself the following questions:

* Is my tariff distance dependent?

  -> Then this model is unfortunately not the right one for you, because only tariffs that are time based can be
  implemented.


* Is my tariff exclusively dependent on the time driven and not on the time of day or the number of days driven?

  -> Then your tariff is a [SlotBasedTariff](#slotbasedtariff).


* Does my tariff depend not only on the duration of the trip, but also on the time of day (e.g. you pay more if you
  travel between eight and nine than if you travel between ten and eleven)?

  -> Then your tariff is a [TimeBasedTariff](#timebasedtariff).


* Is my tariff not only based on the time I ride, but also on the number of days I ride (e.g. if I rent a bike for two
  days, the price per day is more expensive than if I rent the bike for a week)?

  -> Then your tariff is a [DayBasedTariff](#daybasedtariff).

All tariffs are based on a modular slot system. The difference between the three tariff models is only the definition of
the slots and the calling of the slots. Each slot must be assigned a rate. The rate is used for the actual calculation
of the price. There are also different types of rates:

* The [FixedRate](#fixedrate) is the simpler form, with which one can specify a fixed amount, which is computed, as soon
  as this Slot is called.


* With the [TimeBasedRate](#timebasedrate) you can define an interval in which a fixed amount is calculated. With each
  repetition of this interval a fixed amount is added to it. The interval does not have to be completely finished, the
  amount is added when entering the interval.

### SlotBasedTariff

A SlotBasedTariff is structured in such a way that the duration of the trip is split into slots. The slots must start at
zero and continue seamlessly, because all slots are used until the end of the trip to calculate the final price. The
price is calculated for each slot individually and then added up.

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
The second slot starts at two hours and is not limited. The TimeBasedRate belongs to this slot. If you now ride for
three hours, one euro from the second slot is added to the one euro from the first slot, because the interval was
started once. If you travel for five hours, three euros from the second slot are added to the one euro from the first
slot, because the interval was started three times.

It is also possible to define a **billing interval**. This causes that after this interval the slots are calculated from
the beginning.

###### Example 2

```JSON
{
  "type": "SlotBasedTariff",
  "id": 1,
  "currency": "EUR",
  "billingInterval": {
    "timeAmount": 5,
    "timeUnit": "HOURS"
  },
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
        "timeUnit": "MINUTES"
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

Up to a travel time of five hours, this rate is calculated identically to [Example 1](#example-1). After 5 hours, the
first slot is started again and added up. This means that for a trip of eight hours, one euro from the first slot and
one euro from the second slot are added to the four euros from the first billing interval. This is repeated with each
subsequent run of a billing interval.

### DayBasedTariff

The DayBasedTariff can consist of two different types of slots. Only one type of slots is used for calculation. Which
type is used depends on the rental period. Both types can be used in one tariff.

There are the following slot types:

* RentalSynchronizedSlots are identical to the slots of the SlotBasedTariff and are calculated in the same way.*


* The DaySynchronizedSlots are dependent on the number of days driven. So you can realize a cheaper daily rate for a
  longer rental period. Exactly one slot is used for the calculation of the price.

###### Example 3

```Json
{
  "type": "DayBasedTariff",
  "id": 1,
  "currency": "EUR",
  "timeZone": "GMT+1",
  "rates": [
    {
      "type": "TimeBasedRate",
      "id": 2,
      "currency": "EUR",
      "interval": {
        "timeAmount": 30,
        "timeUnit": "MINUTES"
      },
      "pricePerInterval": {
        "credit": 100
      },
      "maxPrice": {
        "credit": 300
      }
    },
    {
      "type": "FixedRate",
      "id": 3,
      "currency": "EUR",
      "price": {
        "credit": 800
      }
    },
    {
      "type": "FixedRate",
      "id": 4,
      "currency": "EUR",
      "price": {
        "credit": 700
      }
    }
  ],
  "slots": [
    {
      "type": "RentalSynchronizedSlot",
      "rate": 2,
      "start": {
        "timeAmount": 0,
        "timeUnit": "NANOSECONDS"
      },
      "end": {
        "timeAmount": 4,
        "timeUnit": "HOURS"
      }
    },
    {
      "type": "DaySynchronisedSlot",
      "rate": 3,
      "startDay": 1,
      "endDay": 3
    },
    {
      "type": "DaySynchronisedSlot",
      "rate": 4,
      "startDay": 3
    }
  ]
}
```

The tariff includes both types of slots. Therefore, the type of calculation depends on the duration of the rental. Up to
a rental period of four hours, the calculation is performed according to the RenalSynchronizedSlots. This happens
identically as with the SlotBasedTariff. So there can be a maximum cost of 3 Euro. If the rental duration is greater
than five hours, the price is calculated according to the RentalSynchronizedSlots. Which slot is used for the
calculation depends on the number of days in use. A day is always from 0 to 24 o'clock. So a rental period from 12
o'clock to 7 o'clock is considered as two days. With two days the second slot is taken for the calculation with the rate
3 with a price per day of 8 euros. Thus, two days cost 16 euros. A rental period from Monday to Saturday will be
calculated as six days. The appropriate slot is the last with the associated rate four and a cost of 7 euros per day.
Thus, the cost is 42 euros.

### TimeBasedTariff

A TimeBasedTariff is also composed of individual slots. However, the calculation does not start with the first slot, but
with the one corresponding to the start time. All slots have a start time and an end time. These times always refer to a
week and consist of a weekday and a time. The slots together must cover a whole week. As with the SlotBasedTariff, a
rate is also assigned to these slots. The calculation works similarly to a SlotBasedTariff, except that the slots
matching the time of day are picked out.

###### Example 4

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

* For the rental of 10 minutes in this slot, you will pay 4€ -> The calculated price is smaller than the minPrice (200
  basePrice + 1 x 100 pricePerInterval < 400 minPrice)
* For the rental of 38 minutes in this slot, you will pay 5€

  200 basePrice

  \+ two times a full interval + 1 started interval -> 3 x 100
  = 500
* For the rental of 140min in this slot, you will pay 10€ -> The calculated price is bigger then the maxPrice (200
  basePrice + 10 x 100 pricePerInterval > 1000 maxPrice)


### Goodwill

To give a tariff a discount, you can add a Goodwill object to each type of tariff. There are three different types of
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
