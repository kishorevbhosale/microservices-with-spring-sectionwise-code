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