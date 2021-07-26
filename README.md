# TradeRepublicDocumentReader

TradeRepublicDocumentReader is a simple Java software that makes available decrypted documents from the [Zweitschriftenversand](https://support.traderepublic.com/de-de/120-Ist-es-m%C3%B6glich-einen-Zweitschriftenversand-einzurichten).

## How To

1. Create an email mailbox and put the credentials into the config.
2. Create a mariadb database and add the credentials to the config.
3. Ask TradeRepublic to set up a [Zweitschriftenversand](https://support.traderepublic.com/de-de/120-Ist-es-m%C3%B6glich-einen-Zweitschriftenversand-einzurichten) for you.
4. You will obtain a password. Put this in the config as well.
5. Create a cronjob and start the program every day.
6. Enjoy

## Build

```bash
mvn package shade:shade
```

## License
[MIT](https://choosealicense.com/licenses/mit/)
