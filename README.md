
# ofsted-forms-notifications

This is a placeholder README.md for a new repository

## Notification template parameters

### Submission

Path: `/submission`

| Paramter | Usage  | 
|----------|--------|
| form-id  | Identifier of form which cause notification |
| submission-time | Moment when form submission happend - formatted with HMRC guidelines |

### Acceptance

Path: `/acceptance`

| Paramter | Usage  | 
|----------|--------|
| form-id  | Identifier of form which cause notification |
| acceptance-time | Moment when form acceptance happend - formatted with HMRC guidelines |

### Rejection

Path: `/rejection`

| Paramter | Usage  | 
|----------|--------|
| form-id  | Identifier of form which cause notification |
| rejection-time | Moment when form rejection happend - formatted with HMRC guidelines |
| url | Url with link to visit after rejection |

## Technical

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
