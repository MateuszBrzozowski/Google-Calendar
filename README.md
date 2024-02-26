### Generating a link to create event in google calendar

Set values such as event name, description, start time and period and get link like below.

```csv
https://calendar.google.com/calendar/u/0/r/eventedit?text=Event+name&details=Description&dates=20240226T120000/20240226T140000
```

Example
```java
EventLink eventLink = new EventLink("Event name");
eventLink.setDescription("Description");
eventLink.setPeriod(120);
eventLink.setStartTime(dateTime);

String link = eventLink.get();
```