package ru.prohor.universe.padawan.kotlin.scripts

fun main() {
    // filename - domain + ".cfg"
    println(generate("google.com", "/var/www/my-app"))
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

        root %s;

        location / {
            try_files $uri /index.html;
        }
    }

    server {
        server_name %s;

         listen 80;
         listen [::]:80;

        return 301 https://$host$request_uri;
    }
""".trimIndent()

private fun generate(domain: String, contentDir: String): String {
    return template.format(domain, domain, domain, contentDir, domain)
}
