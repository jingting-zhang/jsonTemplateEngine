# jsonTemplateEngine
A template render engine that convert the source json format data to another json format data by custom template.
#input:
````
{
    "personal_info":{
        "birthday":"06/21/2000",
        "email_address":"hz*****uck@126.com",
        "gender":"male",
        "name":"zho****e",
        "phone_number":"+08******359"
    },
    "wishlist":[
        {
            "total_item":"9",
            "name":null,
            "items":[
                {
                    "price":"60000",
                    "name":"Goo.N Excelent Soft Premium Pants Jumbo M isi 32",
                    "availability":null
                },
                {
                    "price":"1790000",
                    "name":"YI LITE 4K Action Camera Original International Version - Hitam FREE Kaos Olahraga",
                    "availability":null
                },
                {
                    "price":"265000",
                    "name":"Onix COGNOS Action Camera 1080p CYGNUS - 12MP",
                    "availability":null
                },
                {
                    "price":"339000",
                    "name":"Kurma Sukari - Sukkary 3kg (1dus)",
                    "availability":null
                },
                {
                    "price":"369000",
                    "name":"Paket Mikrofon PROFFESIONAL 2 Microphone Taffware BM700 + Stand Gantung + Pop Filter + Splitter / Paket Smule Karaoke Lazpedia",
                    "availability":null
                },
                {
                    "price":"3299000",
                    "name":"Oppo F3 4GB/64GB Gold\u2013 Smartphone Dual Selfie Camera (Garansi Resmi Oppo Indonesia, Cicilan Tanpa Kartu Kredit, Gratis Ongkir)",
                    "availability":null
                },
                {
                    "price":"165000",
                    "name":"HouseOfOrganix - Natural Whole Almond - 1000gr",
                    "availability":null
                },
                {
                    "price":"499000",
                    "name":"JBL Clip 2 Bluetooth Speaker - Biru",
                    "availability":null
                },
                {
                    "price":"2399000",
                    "name":"Xiaomi Mi A1 64GB - Black - Snapdragon 625",
                    "availability":null
                }
            ]
        },
{
            "total_item":"9",
            "name":null,
            "items":[
                {
                    "price":"60000",
                    "name":"Goo.N Excelent Soft Premium Pants Jumbo M isi 32",
                    "availability":null
                },
                {
                    "price":"1790000",
                    "name":"YI LITE 4K Action Camera Original International Version - Hitam FREE Kaos Olahraga",
                    "availability":null
                },
                {
                    "price":"265000",
                    "name":"Onix COGNOS Action Camera 1080p CYGNUS - 12MP",
                    "availability":null
                },
                {
                    "price":"339000",
                    "name":"Kurma Sukari - Sukkary 3kg (1dus)",
                    "availability":null
                },
                {
                    "price":"369000",
                    "name":"Paket Mikrofon PROFFESIONAL 2 Microphone Taffware BM700 + Stand Gantung + Pop Filter + Splitter / Paket Smule Karaoke Lazpedia",
                    "availability":null
                },
                {
                    "price":"3299000",
                    "name":"Oppo F3 4GB/64GB Gold\u2013 Smartphone Dual Selfie Camera (Garansi Resmi Oppo Indonesia, Cicilan Tanpa Kartu Kredit, Gratis Ongkir)",
                    "availability":null
                },
                {
                    "price":"165000",
                    "name":"HouseOfOrganix - Natural Whole Almond - 1000gr",
                    "availability":null
                },
                {
                    "price":"499000",
                    "name":"JBL Clip 2 Bluetooth Speaker - Biru",
                    "availability":null
                },
                {
                    "price":"2399000",
                    "name":"Xiaomi Mi A1 64GB - Black - Snapdragon 625",
                    "availability":null
                }
            ]
        }
    ],
    "orders":[
        {
            "order_no":"2062******2403",
            "shipping_cost":"26500",
            "sub_total":"2253000",
            "billing_address":{
                "address":"1212***df, A***, Bu***, L***-",
                "name":"hzx at th",
                "phone_number":"08*****4"
            },
            "order_time":"09/05/2018",
            "grand_total":"2279500",
            "shipping_address":{
                "address":"1212***df, A***, Bu***, L***-",
                "name":"hzx at th",
                "phone_number":"08*****4"
            },
            "packages":[
                {
                    "shipping":null,
                    "sold_by":"Lazada E-Services Philippines",
                    "goods":[
                        {
                            "amount":"1",
                            "price":"2253000",
                            "name":"Apple iPhone 6s Plus Gold 32GB"
                        }
                    ],
                    "status":null
                }
            ],
            "promotion":"0"
        }
    ],
    "address_book":{
        "other_address":[
            {
                "area":"B***,K**. T****,T*****n",
                "address":"test2",
                "name":"zh****g",
                "phone_number":"08*****678"
            }
        ],
        "billing_address":{
            "area":null,
            "address":null,
            "name":null,
            "phone_number":null
        },
        "shipping_address":{
            "area":"D** J****,K*** Jak*** U***,K***",
            "address":"ke***an t**u u***a",
            "name":"zh****g",
            "phone_number":"08******59"
        }
    },
    "payment_method":[
        {
            "ends_with":"5719",
            "card_type":"VISA",
            "valid_util":"07/2020"
        }
    ]
}
````

#template:
````
{
  "personal_info": {
    "email_address": "$.personal_info.email_address",
    "phone_number": "$.personal_info.phone_number",
    "name": "$.personal_info.name",
    "gender": "$.personal_info.gender",
    "birthday": "$.personal_info.birthday",
    "verified": "$.personal_info.verified"
  },
  "address_book": {
    "shipping_address":{
      "name":"$.address_book.shipping_address.name",
      "address":"$.address_book.shipping_address.address",
      "area":"$.address_book.shipping_address.area",
      "phone_number":"$.address_book.shipping_address.phone_number"
    },
    "billing_address": {
      "name":"$.address_book.billing_address.name",
      "address":"$.address_book.billing_address.address",
      "area":"$.address_book.billing_address.area",
      "phone_number":"$.address_book.billing_address.phone_number"
    },
    "other_address": [
      {
        "name":"$.address_book.other_address[].name",
        "address":"$.address_book.other_address[].address",
        "area":"$.address_book.other_address[].area",
        "phone_number":"$.address_book.other_address[].phone_number"
      }
    ]
  },
  "orders": [
    {
      "order_no": "$.orders[].order_no",
      "order_time": "$.orders[].order_time",
      "grand_total": "$.orders[].grand_total",
      "sub_total": "$.orders[].sub_total",
      "shipping_cost": "$.orders[].shipping_cost",
      "promotion": "$.orders[].promotion",
      "shipping_address": {
        "name":"$.orders[].shipping_address.name",
        "address":"$.orders[].shipping_address.address",
        "phone_number":"$.orders[].shipping_address.phone_number"
      },
      "billing_address": {
        "name":"$.orders[].billing_address.name",
        "address":"$.orders[].billing_address.address",
        "phone_number":"$.orders[].billing_address.phone_number"
      },
      "packages":[
        {
          "status":"$.orders[].packages[].status",
          "shipping":"$.orders[].packages[].shipping",
          "sold_by":"$.orders[].packages[].sold_by",
          "goods": [
            {
              "name":"$.orders[].packages[].goods[].name",
              "price":"$.orders[].packages[].goods[].price",
              "amount":"$.orders[].packages[].goods[].amount"
            }
          ]
        }
      ]
    }
  ],
  "wishlist": [
    {
      "name": "$.wishlist[].name",
      "total_item": "$.wishlist[].total_item",
      "items": [
        {
          "name": "$.wishlist[].items[].name",
          "availability": "$.wishlist[].items[].availability",
          "price": "$.wishlist[].items[].price"
        }
      ]
    }
  ],
  "payment_method": [
    {
    "card_type":"$.payment_method[].card_type",
    "ends_with":"$.payment_method[].ends_with",
    "valid_until":"$.payment_method[].valid_until"
    }
    ]
}
````
#output:
````
{
    "personal_info":{
        "birthday":"06/21/2000",
        "email_address":"hz*****uck@126.com",
        "gender":"male",
        "name":"zho****e",
        "verified":null,
        "phone_number":"+08******359"
    },
    "wishlist":[
        {
            "total_item":"9",
            "name":null,
            "items":[
                {
                    "price":"60000",
                    "name":"Goo.N Excelent Soft Premium Pants Jumbo M isi 32",
                    "availability":null
                },
                {
                    "price":"1790000",
                    "name":"YI LITE 4K Action Camera Original International Version - Hitam FREE Kaos Olahraga",
                    "availability":null
                },
                {
                    "price":"265000",
                    "name":"Onix COGNOS Action Camera 1080p CYGNUS - 12MP",
                    "availability":null
                },
                {
                    "price":"339000",
                    "name":"Kurma Sukari - Sukkary 3kg (1dus)",
                    "availability":null
                },
                {
                    "price":"369000",
                    "name":"Paket Mikrofon PROFFESIONAL 2 Microphone Taffware BM700 + Stand Gantung + Pop Filter + Splitter / Paket Smule Karaoke Lazpedia",
                    "availability":null
                },
                {
                    "price":"3299000",
                    "name":"Oppo F3 4GB/64GB Gold– Smartphone Dual Selfie Camera (Garansi Resmi Oppo Indonesia, Cicilan Tanpa Kartu Kredit, Gratis Ongkir)",
                    "availability":null
                },
                {
                    "price":"165000",
                    "name":"HouseOfOrganix - Natural Whole Almond - 1000gr",
                    "availability":null
                },
                {
                    "price":"499000",
                    "name":"JBL Clip 2 Bluetooth Speaker - Biru",
                    "availability":null
                },
                {
                    "price":"2399000",
                    "name":"Xiaomi Mi A1 64GB - Black - Snapdragon 625",
                    "availability":null
                }
            ]
        },
        {
            "total_item":"9",
            "name":null,
            "items":[
                {
                    "price":"60000",
                    "name":"Goo.N Excelent Soft Premium Pants Jumbo M isi 32",
                    "availability":null
                },
                {
                    "price":"1790000",
                    "name":"YI LITE 4K Action Camera Original International Version - Hitam FREE Kaos Olahraga",
                    "availability":null
                },
                {
                    "price":"265000",
                    "name":"Onix COGNOS Action Camera 1080p CYGNUS - 12MP",
                    "availability":null
                },
                {
                    "price":"339000",
                    "name":"Kurma Sukari - Sukkary 3kg (1dus)",
                    "availability":null
                },
                {
                    "price":"369000",
                    "name":"Paket Mikrofon PROFFESIONAL 2 Microphone Taffware BM700 + Stand Gantung + Pop Filter + Splitter / Paket Smule Karaoke Lazpedia",
                    "availability":null
                },
                {
                    "price":"3299000",
                    "name":"Oppo F3 4GB/64GB Gold– Smartphone Dual Selfie Camera (Garansi Resmi Oppo Indonesia, Cicilan Tanpa Kartu Kredit, Gratis Ongkir)",
                    "availability":null
                },
                {
                    "price":"165000",
                    "name":"HouseOfOrganix - Natural Whole Almond - 1000gr",
                    "availability":null
                },
                {
                    "price":"499000",
                    "name":"JBL Clip 2 Bluetooth Speaker - Biru",
                    "availability":null
                },
                {
                    "price":"2399000",
                    "name":"Xiaomi Mi A1 64GB - Black - Snapdragon 625",
                    "availability":null
                }
            ]
        }
    ],
    "orders":[
        {
            "order_no":"2062******2403",
            "shipping_cost":"26500",
            "sub_total":"2253000",
            "billing_address":{
                "address":"1212***df, A***, Bu***, L***-",
                "name":"hzx at th",
                "phone_number":"08*****4"
            },
            "order_time":"09/05/2018",
            "grand_total":"2279500",
            "shipping_address":{
                "address":"1212***df, A***, Bu***, L***-",
                "name":"hzx at th",
                "phone_number":"08*****4"
            },
            "packages":[
                {
                    "shipping":null,
                    "sold_by":"Lazada E-Services Philippines",
                    "goods":[
                        {
                            "amount":"1",
                            "price":"2253000",
                            "name":"Apple iPhone 6s Plus Gold 32GB"
                        }
                    ],
                    "status":null
                }
            ],
            "promotion":"0"
        }
    ],
    "address_book":{
        "other_address":[
            {
                "area":"B***,K**. T****,T*****n",
                "address":"test2",
                "name":"zh****g",
                "phone_number":"08*****678"
            }
        ],
        "billing_address":{
            "area":null,
            "address":null,
            "name":null,
            "phone_number":null
        },
        "shipping_address":{
            "area":"D** J****,K*** Jak*** U***,K***",
            "address":"ke***an t**u u***a",
            "name":"zh****g",
            "phone_number":"08******59"
        }
    },
    "payment_method":[
        {
            "ends_with":"5719",
            "valid_until":null,
            "card_type":"VISA"
        }
    ]
}
````
