# 2 Assets in the provider catalog

## Assets
For this example first er need to create 2 different assets, then make the request to the both assests.

curl -d @transfer/transfer-01-negotiation/resources/create-asset.json \
  -H 'content-type: application/json' http://localhost:19193/management/v3/assets \
  -s | jq

## Policy
Then we have to create the policy, in this case we are going to use the same policy, a blank one for both asses.

curl -d @transfer/transfer-01-negotiation/resources/create-policy.json \
  -H 'content-type: application/json' http://localhost:19193/management/v2/policydefinitions \
  -s | jq

## Contract
For the contract we will create a standar blank contract for both assests too.

curl -d @transfer/transfer-01-negotiation/resources/create-contract-definition.json \
  -H 'content-type: application/json' http://localhost:19193/management/v2/contractdefinitions \
  -s | jq

## Catalog
After doing all this, we will check the provider's catalog, where both assets will appear, each with its contract ID that we will need to access that asset later (dcat:dataset.odrl:hasPolicy.@id). 

curl -X POST "http://localhost:29193/management/v2/catalog/request" \
    -H 'Content-Type: application/json' \
    -d @transfer/transfer-01-negotiation/resources/fetch-catalog.json -s | jq

## Negotiation
Once we have the IDs, we will need to negotiate the contract. To do this, we will include in the contract negotiation files, negotiate-contract.json, the ID provided by the catalog and the ID of the asset they refer to. We will make the negotiation request for each of the contracts. 

curl -d @transfer/transfer-01-negotiation/resources/negotiate-contract.json \
  -X POST -H 'content-type: application/json' http://localhost:29193/management/v2/contractnegotiations \
  -s | jq

Now we need the agreementID, for this we run.

curl -X GET "http://localhost:29193/management/v2/contractnegotiations/{{contract-negotiation-id}}" \
    --header 'Content-Type: application/json' \
    -s | jq

## Logger
Before moving on to the next step, we must set up a server with Docker to obtain data exchange logs. This server will listen on port 4000, as indicated in the connector configuration. Once the negotiation is done, we will obtain the contractAgreementID.

docker build -t http-request-logger util/http-request-logger
docker run -p 4000:4000 http-request-logger

## Transfer
Then, we will need to add each one in our transfer file, start-transfer.json, the agreementID with the ID of the asset it refers to. Once done, we will initiate both transfers. 
 
curl -X POST "http://localhost:29193/management/v2/transferprocesses" \
  -H "Content-Type: application/json" \
  -d @transfer/transfer-02-consumer-pull/resources/start-transfer.json \
  -s | jq

## Get the data
After completing the transfer, we will be able to access the data with the authcode generated in the Docker server logs. Each authcode will give us access to an asset, identified by the contractID.

curl --location --request GET 'http://localhost:29291/public/' --header 'Authorization: <auth code>'


```mermaid
sequenceDiagram
participant Consumer
participant Provider


User->>Provider: Check provider's catalog
Provider-->>Consumer: Display assets with contract IDs
User->>Provider: Negotiate contract for each asset
Provider-->>Consumer: Provide contractAgreementID

User->>Provider: Add contractAgreementID to transfer file
User->>Provider: Initiate transfer for each asset
Server-->>Consumer: Provide authcode for data access



