
# ofsted-forms-notifications

This is a placeholder README.md for a new repository

## Notification template parameters

All templates require JSON formatted body.

Basic strcture 

```json
{
  "time" : "2004-02-12T15:19:21+00:00",
  "email" : "jan.kowalski@example.com"
  "id" : "da7740d5-6026-4cdd-bbc1-10cb077cc47b"
}

```

| field | Usage |
| id | identifier of submitted form - could be arbitrarry string |
| time  | when notified event happend. Formatted agins ISO8601 |
| email | email where we should send notification |

### Submission

Path: `/submission`

#### Gov Notify template paramters

| Paramter | Usage  | 
|----------|--------|
| form-id  | Identifier of form which cause notification |
| submission-time | Moment when form submission happend - formatted with HMRC guidelines |

### Acceptance

Path: `/acceptance`

#### Gov Notify template paramters

| Paramter | Usage  | 
|----------|--------|
| form-id  | Identifier of form which cause notification |
| acceptance-time | Moment when form acceptance happend - formatted with HMRC guidelines |

### Rejection

Path: `/rejection`

#### Gov Notify template paramters

| Paramter | Usage  | 
|----------|--------|
| form-id  | Identifier of form which cause notification |
| rejection-time | Moment when form rejection happend - formatted with HMRC guidelines |
| url | Url with link to visit after rejection |

## Technical

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
