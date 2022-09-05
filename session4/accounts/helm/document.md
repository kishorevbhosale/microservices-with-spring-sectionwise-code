Installation Details
---

**Windows Installation :**
1) First install chocolatey which is package manager for windows : https://chocolatey.org/ -> install
2) Refer **Install Chocolatey for Individual Use**
3) Go to https://helm.sh/ and refer installing helm **From Chocolatey (Windows)**<br />
   <code>PS C:\WINDOWS\system32> helm version<br />
   version.BuildInfo{Version:"v3.9.3", GitCommit:"414ff28d4029ae8c8b05d62aa06c7fe3dee2bc58", GitTreeState:"clean", GoVersion:"go1.17.13"}</code>
4) create the dir `helm` under `accounts`
5) Open the terminal and run below commands :<br />
   - first connect terminal with your cluster by copying connect command from gcp
   - `> helm ls` - check the deployment details
   - `> helm create skbank-common` - it will create the default dir with all the .yml files (chart, value deployment, ingress...)
   - `> helm install sample-deployment skbank-common` 
   - go to gcp workload section and check the deployment 
   - by default `ingress enabled` property is `false` change it to `true` by editing `ingress.yml` file under `skbank-common` -> `templates` dir
   - `> helm upgrade sample-deployment skbank-common` 
   - go to gcp -> service and ingress section -> ingress (will take few min to create the ingress)
   - after creating ingress if you click on Frontend url -> no ip mapping so will not work
   - click on name and in details -> ip address -> update this ip adress in host file
   - open the default Frontend link -> you will get nginx page
   - `> helm uninstall sample-deployment` this single command deletes all your deployments

**Create service charts :** 
1) create folder skbank-service
2) open the terminal inside `skbank-service`
3) run the command to create accounts related helm chart `> helm create account`
4) update the `dependency` in `Charts.yml`
5) update the `values.yml`
6) add `deployment.yaml` and `service.yaml` files.
7) run the command to add dependency `> helm dependency build`
8) follow step 3 to 7 for different services (cards, loans, configserver, eurekaserver, gatewayserver, zipkin)

**Create env specific charts**
1) create dir `environment` under this create charts `dev-env` and `prod-env` using command `> helm create dev-env`
2) Add dependencies in `Chart.yml` in both prod-env and dev-env
3) Update values in `values.yml` in both prod-env and dev-env
4) Add `configmap.yml` in both prod-env and dev-env
5) run the command to add dependency `> helm dependency build`
6) Verify dependencies added in `charts` dir
7) check your manifest file created properly -> go to prod-env and run the command `> helm template .` sim. try in dev-env


**Install Helm charts into k8s cluster :**
1) go to `environment` dir and run the command `> helm install dev-deployment dev-env` will give following type of output
```aidl
W0905 09:03:03.396243   12344 gcp.go:120] WARNING: the gcp auth plugin is deprecated in v1.22+, unavailable in v1.25+; use gcloud instead.
To learn more, consult https://cloud.google.com/blog/products/containers-kubernetes/kubectl-auth-changes-in-gke
NAME: dev-deployment
LAST DEPLOYED: Mon Sep  5 09:03:05 2022
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
```
2) Check details in `Secrets and ConfigMaps` in GCP
3) Check all services are running in `Services & Ingress` in GCP
4) Check Endpoints for all services listed inside GCP