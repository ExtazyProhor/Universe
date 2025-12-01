package ru.prohor.universe.kt.padawan.scripts

fun main() {
    // filename - domain + ".cfg"
    println(generate("google.com", "7777"))
}

private val template = $$"""
    server {
        server_name %s;

        listen [::]:443 ssl;
        listen 443 ssl;

        ssl_certificate /etc/letsencrypt/live/%s/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/%s/privkey.pem;
        include /etc/letsencrypt/options-ssl-nginx.conf;
        ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;

        location / {
            proxy_pass http://127.0.0.1:%s;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }

    server {
        server_name %s;

         listen 80;
         listen [::]:80;

        return 301 https://$host$request_uri;
    }
""".trimIndent()

private fun generate(domain: String, port: String): String {
    return template.format(domain, domain, domain, port, domain)
}
