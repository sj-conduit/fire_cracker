from flask import Flask, request, jsonify
import logging

app = Flask(__name__)

logging.basicConfig(filename='sms_log.txt', level=logging.DEBUG)

@app.route('/sms-receive', methods=['POST'])
def sms_receive():
    data = request.get_json()
    # print(f"Received data: {data}")
    logging.debug(f"Received data: {data}")
    from_number = data.get('from')
    message_body = data.get('text')
    logging.info(f"Received SMS from {from_number}: {message_body}")
    return jsonify({"status": "success"})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, ssl_context=('/home/contact_samju/cert.pem', '/home/contact_samju/key.pem'))
