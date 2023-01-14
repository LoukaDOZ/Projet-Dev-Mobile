curl https://api.todoist.com/sync/v9/sync \
    -H "Authorization: Bearer 8bd022ec1329a5066aed62f5c816322a92133a81" \
    -d commands='[
    {
        "type": "user_update", 
        "uuid": "52f83009-7e27-4b9f-9943-1c5e3d1e6901", 
        "args": {
            "current_password": "GuillaumeEtLouka",
            "email": "guillaume.descroix91220a@gmail.com",
	    "full_name": "Guillaume Descroixx"
        }
    }]'
