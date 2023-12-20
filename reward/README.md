## Init project

```bash
python -m venv venv
pip install -r requirements.txt
```

## Refresh Django migrations
```bash
python manage.py migrate fulfillments zero
rm -rf fulfillments/migrations
python manage.py makemigrations fulfillments
python manage.py migrate fulfillments
```

## Build Docker
```bash
docker build -t reward:0.0.1 . 
```