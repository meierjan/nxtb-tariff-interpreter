# Tariff Interpreter

___
This Interpreter is a reverence implementation for computing a JSON formatted bike-sharing tariff model.

## Contents

___

* [Why this interpreter](#why-this-tariff-schema)
* [General Interpreter Operation](#general-interpreter-operation)
* [Tariff JSON Schemas](#tariff-json-schema)

## Why this Tariff-Schema

___
There are two main reasons why the creation of a new pricing model has begun:

* our current style for represent price tariffs are functions. On the one hand, this has the disadvantage that it leads
  to inconsistencies between the tariffs and, on the other hand, it is difficult to create and read a tariff.
* The old model was not able to provide a transparent breakdown of costs incurred similar to a receipt.

These points were tried to improve specifically.

## General Interpreter Operation

___
The Interpreter requires a [Tariff JSON Object](#tariff-json-schema) and a rental period.

## Tariff JSON-Schema

___
Basically, there are three types of tariffs:

* [SlotBasedTariff:](#slotbasedtariff)

This type of tariff can represent tariff that depend only on the duration of a rental.

* [DayBasedTariff:](#daybasedtariff)

  These tariffs depend not only on the rental period, but also on the number of days started.
* [TimeBasedTariff:](#timebasedtariff)

  These tariffs depend not only on the rental period, but also on the time of day. It is important to cover a whole
  week.

To make it as simple as possible, all tariffs consist of a modular principle. This allows you to build up your tariff by
adding slots and rates to your tariff

### SlotBasedTariff

___

| Field Name      | Required |                Type                | Defines                                                                           |
|-----------------|:--------:|:----------------------------------:|-----------------------------------------------------------------------------------|
| type            |   Yes    |               String               | Necessary to identify the tariff type. In this case 'SlotBasedTariff              |
| id              |   Yes    |                Long                | Unique identifier of the tariff                                                   |
| currency        |   Yes    |               String               | Currency used for the rental [(ISO 4217)](https://de.wikipedia.org/wiki/ISO_4217) |
| goodwill        | Optional |       [Goodwill](#goodwill)        | Object to add a goodwill defined                                                  |
| billingInterval | Optional |       [Interval](#interval)        | Creates a loop to reset the credit period                                         |
| rates           |   Yes    |      Array of [Rates](#rate)       | Array that contains all rates of a tariff                                         |
| slots           |   Yes    | Array of [Slots](#slot-based-slot) | Array that contains all slots of a tariff                                         |

###### Example

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
        "timeAmount": 1,
        "timeUnit": "HOURS"
      },
      "pricePerInterval": {
        "credit": 100
      },
      "maxPrice": {
        "credit": 1500
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

* For a rental period of 20 minutes you pay 1€.
* For a rental period of 2 hours and 45 minutes you pay 2€

  100 for the first 2 hours (fixedRate)

  \+ 100 for the started interval (45min)

  = 200

* For a rental period of 1 day and 30minutes you pay 17€

  100 for the first 2 hours

  \+ 1500 (maxPrice) for the second slot

  \+ 100 for the last 30 minutes (it starts again with the first slot because the billingInterval was exceeded)

* = 1700

#### Slot Based Slot

| Field Name | Required  |         Type          | Defines                     |
|------------|:---------:|:---------------------:|-----------------------------|
| rate       |    Yes    |         Long          | Unique identifier of a rate |
| start      |    Yes    | [Interval](#interval) | Start-time for slot         |
| end        | Optional  | [Interval](#interval) | End-time for slot           |

### DayBasedTariff

---

| Field Name      | Required |                Type                | Defines                                                                           |
|-----------------|:--------:|:----------------------------------:|-----------------------------------------------------------------------------------|
| type            |   Yes    |               String               | Necessary to identify the tariff type. In this case 'DayBasedTariff'              |
| id              |   Yes    |                Long                | Unique identifier of a tariff                                                     |
| currency        |   Yes    |               String               | Currency used for the rental [(ISO 4217)](https://de.wikipedia.org/wiki/ISO_4217) |
| timeZone        |   Yes    |               String               | The time zone where the customer is locate (GMT/UTC/UT+prefix)                    |
| goodwill        | Optional |       [Goodwill](#goodwill)        | Object to add a goodwill defined                                                  |
| billingInterval | Optional |       [Interval](#interval)        | Generates a loop to reset the credit period                                       |
| rates           |   Yes    |      Array of [Rates](#rate)       | Array that contains all rates of a tariff                                         |
| slots           |   Yes    | Array of [Slots](#day-based-slots) | Array that contains all slots of a tariff                                         |                                                                                                           |

###### Example

```Json
{
  "type": "DayBasedTariff",
  "id": 1,
  "currency": "EUR",
  "timeZone": "GMT+1",
  "goodwill": {
    "type": "StaticGoodwill",
    "duration": {
      "timeAmount": 10,
      "timeUnit": "MINUTES"
    }
  },
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

* For a rental period of 95 minutes you pay 3€ (95 minutes - 10 minutes staticGoodwill = 85 minutes -> 3 * 100)
* For a rental period from Monday 7 am to Monday 5 pm you pay 8 €.
  * the duration is longer than 3 hours -> the first slot is not usable.
  * The count of days rented is one -> the second slot is usable -> 1 * 8€
* For a rental period from Monday 5 pm to Tuesday 3 am you pay 16 €.
  * the duration is longer than 3 hours -> the first slot is not usable.
  * The count of days rented is two -> the second slot is usable -> 2 * 8€
* For a rental period from Monday 5 pm to Wednesday 6am am you pay 21€.
  * the duration is longer than 3 hours -> the first slot is not usable.
  * the duration is longer than 2 Days -> the second slot is not usable.
  * The count of days rented is three -> the second slot is usable ->  3 * 7€

#### Day based Slots

There are basically two kinds of slots. You can mix these kinds in a tariff.

* [RentalSynchronizedSlot](#slot-based-slot)

  These Slots are absolutely identically to the [Slot based Slots](#slot-based-slot). The price calculation depends only
  on the rental period.
* [DaySynchronizedSlot](#daysynchronizedslot)

  This type of slot depends on the rental period and the number of days rented. So you can make the daily price
  depending on the number of rented days. All days are charged with the same slot and rate. You must only add the type
  field with the value 'RentalSynchronizedSlot'

##### DaySynchronizedSlot

| Field Name | Required |  Type  | Defines                                                                 |
|------------|:--------:|:------:|-------------------------------------------------------------------------|
| type       |   Yes    | String | Necessary to identify the slot type. In this case 'DaySynchronizedSlot' |
| rate       |   Yes    |  Long  | Unique identifier of a rate                                             |
| startDay   |   Yes    |  Int   | Minimum total time in days to apply this slot                           | 
| endDay     | Optional |  Int   | Maximum total time in days to apply this slot                           |

###### Example

```Json
{
  "type": "DaySynchronizedSlot",
  "rate": 1,
  "startDay": 1,
  "endDay": 3
}
```

If the number of days rented is between 1 and 3, rate 1 is charged for each day.

### TimeBasedTariff

___

| Field Name      | Required |              Type               | Defines                                                                           |
|-----------------|:--------:|:-------------------------------:|-----------------------------------------------------------------------------------|
| type            |   Yes    |             String              | Necessary to identify the tariff type. In this case 'TimeBasedTariff'             |
| id              |   Yes    |              Long               | Unique identifier of the tariff                                                   |
| currency        |   Yes    |             String              | Currency used for the rental [(ISO 4217)](https://de.wikipedia.org/wiki/ISO_4217) |
| timeZone        |   Yes    |             String              | The time zone where the customer is locate (GMT/UTC/UT+prefix)                    |
| goodwill        | Optional |      [Goodwill](#goodwill)      | Object to add a goodwill defined                                                  |
| billingInterval | Optional |      [Interval](#interval)      | Generates a loop to reset the credit period                                       |
| rates           |   Yes    |     Array of [Rates](#rate)     | Array that contains all rates of a tariff                                         |
| timeSlots       |   Yes    | Array of [TimeSlots](#timeslot) | Defines day time dependent slots                                                  |

###### Example

```Json
{
  "type": "TimeBasedTariff",
  "id": 1,
  "currency": "EUR",
  "timeZone": "GMT+1",
  "goodwill": {
    "type": "FreeMinutes",
    "duration": {
      "timeAmount": 5,
      "timeUnit": "MINUTES"
    }
  },
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

* If your rental is from Monday 8am to Wednesday 10pm you pay once 1€.
* If you rent from Friday 10pm to Sunday 10am you pay once 2€.
* If you rent from Monday 8am to Saturday 10am, you pay €3 because you cut both time periods.

#### TimeSlot

| Field Name | Required |      Type      | Defines                            |
|------------|:--------:|:--------------:|------------------------------------|
| rate       |   Yes    |      Long      | Unique identifier of a rate        |
| from       |   Yes    | [Time](#time)  | Defines start time for a time slot | 
| to         |   Yes    | [Time](#time)  | Defines end time for a time slot   | 

###### Example

```Json
{
  "rate": 1,
  "from": {
    "day": "MONDAY",
    "hour": "5",
    "minutes": 30
  },
  "to": {
    "day": "FRIDAY",
    "hour": "18",
    "minutes": 45
  }
}
```

##### Time

| Field Name | Required |                                      Type                                       | Defines                      |
|------------|:--------:|:-------------------------------------------------------------------------------:|------------------------------|
| day        |   Yes    | [DayOfWeek](https://docs.oracle.com/javase/8/docs/api/java/time/DayOfWeek.html) | Defines day of week          |
| hour       |   Yes    |                                       Int                                       | Defines hour of a day (1-24) |
| minutes    |   Yes    |                                       Int                                       | Defines minutes of a hour    |

###### # Example

```Json
{
  "day": "MONDAY",
  "hour": "5",
  "minutes": 30
}
```

### Objects used in all Tariffs

___

#### Goodwill

Basically there are three types you can choose for a goodwill:

* [StaticGoodwill:](#staticgoodwill)

  Subtracts a static time from the rental duration from behind
* [DynamicGoodwill:](#dynamicgoodwill)

  Subtracts a dynamic time from the rental duration from behind
* [FreeMinutes:](#freeminutes)

Subtracts a static time from the rental period from the start

##### StaticGoodwill

| Field Name | Required |         Type          | Defines                                                               |
|------------|:--------:|:---------------------:|-----------------------------------------------------------------------|
| type       |   Yes    |        String         | Necessary to identify the goodwill type. In this case 'StaticGoodwill |
| duration   |   Yes    | [Interval](#interval) | Static time amount of time deducted from the end of the rent          |

###### Example

```Json
{
  "type": "StaticGoodwill",
  "duration": {
    "timeAmount": 100,
    "timeUnit": "SECONDS"
  }
}
```

##### DynamicGoodwill

| Field Name                       | Required |  Type  | Defines                                                                  |
|----------------------------------|:--------:|:------:|--------------------------------------------------------------------------|
| type                             |   Yes    | String | Necessary to identify the goodwill type. In this case 'DynamicGoodwill'  |
| deductibleProportionInPercentage |   Yes    | Float  | Dynamic percentage value  deducted from the end of the rent              |

###### Example:

```Json
{
  "type": "DynamicGoodwill",
  "deductibleProportionInPercentage": 10.0
}
```

So 10% of the rental period will be deducted

##### FreeMinutes

| Field Name | Required |         Type          | Defines                                                              |
|------------|:--------:|:---------------------:|----------------------------------------------------------------------|
| type       |   Yes    |        String         | Necessary to identify the goodwill type. In this case 'FreeMinutes'  |
| duration   |   Yes    | [Interval](#interval) | Static time amount of time deducted from the start of the rent       |

```Json
{
  "type": "FreeMinutes",
  "duration": {
    "timeAmount": 10,
    "timeUnit": "MINUTES"
  }
}
```

#### Interval

| Field Name | Required |                                                   Type                                                    | Defines                                                                                                                               |
|------------|:--------:|:---------------------------------------------------------------------------------------------------------:|---------------------------------------------------------------------------------------------------------------------------------------|
| timeAmount |   Yes    |                                                    Int                                                    | Defines time amount                                                                                                                   |
| timeUnit   |   Yes    | [String](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/TimeUnit.html) | Defines [time unit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/TimeUnit.html) for the interval |

###### Example

```Json
{
  "timeAmount": 3,
  "timeUnit": "HOURS"
}
```

### Price

| Field Name | Required | Type | Defines                   |
|------------|:--------:|:----:|---------------------------|
| credit     |   Yes    | Int  | Defines the price amount  |

###### Example

```JSON
{
  "currency": "EUR",
  "price": {
    "credit": 100
  }
}
```

This means __1€__ not ~~100€~~

#### Rate

Basically there are two types of __rates__, and you can mix all rates in the rate array of a tariff.

* [FixedRate](#fixedrate)

  The price of a FixedRate is calculated once per started slot.
* [TimeBasedRate:](#timebasedrate)

  The price for this rate depends on the rental period of the rate.

##### FixedRate

| Field Name | Required |      Type       | Defines                                                                            |
|------------|:--------:|:---------------:|------------------------------------------------------------------------------------|
| type       |   Yes    |     String      | Necessary to identify the slot type. In this case 'FixedRate'                      |
| id         |   Yes    |      Long       | Unique identifier of this rate                                                     | 
| currency   |   Yes    |     String      | Currency used to pay the rate [(ISO 4217)](https://de.wikipedia.org/wiki/ISO_4217) |
| price      |   Yes    | [Price](#price) | Rate price                                                                         | 

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

| Field Name       | Required |         Type          | Defines                                                                            |
|------------------|:--------:|:---------------------:|------------------------------------------------------------------------------------|
| type             |   Yes    |        String         | Necessary to identify the slot type. In this case 'TimeBasedRate'                  |
| id               |   Yes    |         Long          | Unique identifier of this rate                                                     | 
| currency         |   Yes    |        String         | Currency used to pay the rate [(ISO 4217)](https://de.wikipedia.org/wiki/ISO_4217) |
| basePrice        | Optional |    [Price](#price)    | Price calculated per started slot                                                  |
| interval         |   Yes    | [Interval](#interval) | Interval in which the price of this rate will reapplied                            |
| pricePerInterval |   Yes    |    [Price](#price)    | Price calculated per interval                                                      | 
| maxPrice         | Optional |    [Price](#price)    | Maximal price for this rate                                                        |
| minPrice         | Optional |    [Price](#price)    | Minimal price for this rate                                                        |

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
  basePrice + 1 * 100 pricePerInterval < 400 minPrice)
* For the rental of 38 minutes in this slot, you will pay 5€

  200 basePrice

  \+ two times a full interval + 1 started interval -> 3 * 100

  = 500
* For the rental of 140min in this slot, you will pay 10€ -> The calculated price is bigger then the maxPrice (200
  basePrice + 10 * 100 pricePerInterval > 1000 maxPrice)