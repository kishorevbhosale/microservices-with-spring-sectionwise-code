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

**Upgrade the deployment :**
1) update the instance of services(accounts, cards, loans) from 1 to 2.<br/>
For that go to dir of respective service and edit the `replicaCount` value from `values.yml` file.
2) All the dependency are added inside the `dev-env` and `prod-env`, <br/>
update this dependencies -> go to `dev-env` and run `> helm dependency build`  sim for `prod-env`
3) From `environment` dir run the command `> helm upgrade dev-deployment dev-env`
4) Check the pod count its changed from 1 to 2 in GCP


**Helm History :**
```aidl
> helm history dev-deployment
W0905 22:29:51.252319   20248 gcp.go:120] WARNING: the gcp auth plugin is deprecated in v1.22+, unavailable in v1.25+; use gcloud instead.
To learn more, consult https://cloud.google.com/blog/products/containers-kubernetes/kubectl-auth-changes-in-gke
REVISION        UPDATED                         STATUS          CHART           APP VERSION     DESCRIPTION
1               Mon Sep  5 21:56:26 2022        superseded      dev-env-0.1.0   1.16.0          Install complete
2               Mon Sep  5 22:26:22 2022        deployed        dev-env-0.1.0   1.16.0          Upgrade complete
```

**Helm Rollback :**
```aidl
>helm rollback dev-deployment 1
W0905 22:32:30.361609   10932 gcp.go:120] WARNING: the gcp auth plugin is deprecated in v1.22+, unavailable in v1.25+; use gcloud instead.
To learn more, consult https://cloud.google.com/blog/products/containers-kubernetes/kubectl-auth-changes-in-gke
Rollback was a success! Happy Helming!
```
**Check history after rollback :**
```aidl
> helm history dev-deployment
W0905 22:33:30.432641   12832 gcp.go:120] WARNING: the gcp auth plugin is deprecated in v1.22+, unavailable in v1.25+; use gcloud instead.
To learn more, consult https://cloud.google.com/blog/products/containers-kubernetes/kubectl-auth-changes-in-gke
REVISION        UPDATED                         STATUS          CHART           APP VERSION     DESCRIPTION
1               Mon Sep  5 21:56:26 2022        superseded      dev-env-0.1.0   1.16.0          Install complete
2               Mon Sep  5 22:26:22 2022        superseded      dev-env-0.1.0   1.16.0          Upgrade complete
3               Mon Sep  5 22:32:31 2022        deployed        dev-env-0.1.0   1.16.0          Rollback to 1
```
**Helm chart uninstall :**
```aidl
> helm uninstall dev-deployment
```


Securing Microservices using K8s Cluster:
---
1) We have 3 services (accounts, cards, loans) which can be accessed directly by using IP address of individual service
2) We can access 3 services using gatewayserver as well 
3) So here securing services, authorization and authentication of service is main challenge.

**_ClusterIP :_**
1) In each service changed the service:type value from `LoadBalancer` to `ClusterIP` 
2) By default the type is `ClusterIP` so if we keep this field as empthy then also it will treat as `ClusterIP`
3) Updated `accounts` image with hostname

**_NodePort :_**
1) Changed `account` service values from `dev-env` dir -> `service: type: NodePort`
2) Port number is not mentioned so it will take the default port number in range between (30k to 32k)
3) Rebuild chart `> helm dependencies build`
4) Upgrage the deployment `> helm upgrade sample-deployment dev-env`
5) check nodes available in cluster `> kubectl get nodes`
```aidl
W0909 05:44:05.082031   14400 gcp.go:120] WARNING: the gcp auth plugin is deprecated in v1.22+, unavailable in v1.25+; use gcloud instead.
To learn more, consult https://cloud.google.com/blog/products/containers-kubernetes/kubectl-auth-changes-in-gke
NAME                                       STATUS   ROLES    AGE   VERSION
gke-cluster-1-default-pool-cd5ab4c4-9vmp   Ready    <none>   24m   v1.22.11-gke.400
gke-cluster-1-default-pool-cd5ab4c4-lltx   Ready    <none>   24m   v1.22.11-gke.400
gke-cluster-1-default-pool-cd5ab4c4-szmm   Ready    <none>   24m   v1.22.11-gke.400
```
But here we didnt get info about which is worker node

6) To get the worker node details : `> kubectl get nodes --output wide`
```aidl
W0909 05:44:34.756100   17788 gcp.go:120] WARNING: the gcp auth plugin is deprecated in v1.22+, unavailable in v1.25+; use gcloud instead.
To learn more, consult https://cloud.google.com/blog/products/containers-kubernetes/kubectl-auth-changes-in-gke
NAME                                       STATUS   ROLES    AGE   VERSION            INTERNAL-IP   EXTERNAL-IP      OS-IMAGE                             KERNEL-VERSION   CONTAINER-RUNTIME
gke-cluster-1-default-pool-cd5ab4c4-9vmp   Ready    <none>   25m   v1.22.11-gke.400   10.128.0.40   x.x.x.x         Container-Optimized OS from Google   5.10.109+        containerd://1.5.13
gke-cluster-1-default-pool-cd5ab4c4-lltx   Ready    <none>   25m   v1.22.11-gke.400   10.128.0.38   x.x.x.x         Container-Optimized OS from Google   5.10.109+        containerd://1.5.13
gke-cluster-1-default-pool-cd5ab4c4-szmm   Ready    <none>   25m   v1.22.11-gke.400   10.128.0.39   x.x.x.x         Container-Optimized OS from Google   5.10.109+        containerd://1.5.13
```
7) Now check account service is running in which pods : `> kubectl get pods`
```aidl
NAME                                        READY   STATUS    RESTARTS      AGE
accounts-deployment-d8489b579-bvvqq         1/1     Running   2 (26m ago)   26m
accounts-deployment-d8489b579-f2m9h         1/1     Running   1 (26m ago)   26m
cards-deployment-945cb65-bs5bk              1/1     Running   2 (26m ago)   26m
configserver-deployment-5457948984-g955b    1/1     Running   0             26m
eurekaserver-deployment-7cc7bcc694-lnhxt    1/1     Running   2 (26m ago)   26m
gatewayserver-deployment-685c68554c-blnhn   1/1     Running   1 (26m ago)   26m
loans-deployment-556647c845-f7jr5           1/1     Running   0             26m
zipkin-deployment-69c646f9fd-7wzfl          1/1     Running   0             26m
```
8) Now describe the pod details of account service to find the node details `> kubectl describe pod <pod-name>`  
```aidl
> kubectl describe pod accounts-deployment-d8489b579-bvvqq
Name:         accounts-deployment-d8489b579-bvvqq
Namespace:    default
Priority:     0
Node:         gke-cluster-1-default-pool-cd5ab4c4-lltx/10.128.0.38
Start Time:   Fri, 09 Sep 2022 05:22:52 +0530
Labels:       app=accounts
              pod-template-hash=d8489b579
Annotations:  <none>
Status:       Running
IP:           10.4.0.6
IPs:
  IP:           10.4.0.6
Controlled By:  ReplicaSet/accounts-deployment-d8489b579
Containers:
  accounts:
    Container ID:   containerd://970628b746114c8bf7acf9bfe0aa92e796bddc9ca8306f8e99d69ba96f870911
    Image:          kishorevbhosale/accounts:latest
    Image ID:       docker.io/kishorevbhosale/accounts@sha256:41183f6fdc4397499d874e2a87f8fc901530cdab2d8b485b588409a393b87108
    Port:           8080/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Fri, 09 Sep 2022 05:23:42 +0530
    Last State:     Terminated
      Reason:       Error
      Exit Code:    1
      Started:      Fri, 09 Sep 2022 05:23:23 +0530
      Finished:     Fri, 09 Sep 2022 05:23:26 +0530
    Ready:          True
    Restart Count:  2
    Environment:
      SPRING_PROFILES_ACTIVE:                <set to the key 'SPRING_PROFILES_ACTIVE' of config map 'skbankdev-configmap'>                Optional: false
      SPRING_ZIPKIN_BASEURL:                 <set to the key 'SPRING_ZIPKIN_BASEURL' of config map 'skbankdev-configmap'>                 Optional: false
      SPRING_CONFIG_IMPORT:                  <set to the key 'SPRING_CONFIG_IMPORT' of config map 'skbankdev-configmap'>                  Optional: false
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE:  <set to the key 'EUREKA_CLIENT_SERVICEURL_DEFAULTZONE' of config map 'skbankdev-configmap'>  Optional: false
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-55px8 (ro)
Conditions:
  Type              Status
  Initialized       True
  Ready             True
  ContainersReady   True
  PodScheduled      True
Volumes:
  kube-api-access-55px8:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    ConfigMapOptional:       <nil>
    DownwardAPI:             true
QoS Class:                   BestEffort
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type     Reason     Age                From               Message
  ----     ------     ----               ----               -------
  Normal   Scheduled  30m                default-scheduler  Successfully assigned default/accounts-deployment-d8489b579-bvvqq to gke-cluster-1-default-pool-cd5ab4c4-lltx
  Normal   Pulled     30m                kubelet            Successfully pulled image "kishorevbhosale/accounts:latest" in 20.40467642s
  Normal   Pulled     30m                kubelet            Successfully pulled image "kishorevbhosale/accounts:latest" in 420.480318ms
  Warning  BackOff    30m                kubelet            Back-off restarting failed container
  Normal   Pulling    29m (x3 over 30m)  kubelet            Pulling image "kishorevbhosale/accounts:latest"
  Normal   Created    29m (x3 over 30m)  kubelet            Created container accounts
  Normal   Started    29m (x3 over 30m)  kubelet            Started container accounts
  Normal   Pulled     29m                kubelet            Successfully pulled image "kishorevbhosale/accounts:latest" in 422.458135ms
```
9) check all the running project inside gcp `> gcloud projects list`
```aidl
PROJECT_ID             NAME              PROJECT_NUMBER
sbtest-150402          sbTest            411582550214
symmetric-lock-360912  My First Project  984566451572
utopian-button-151310  My Project        263431673942
```

10) Make `sbtest-150402 ` as your default project `> gcloud config set project sbtest-150402` **Firewall configuration set for this project only**
11) Allow traffic to your service `> gcloud compute firewall-rules create skbankaccount-nodeport --allow tcp:32713`
```aidl
Creating firewall...-Created [https://www.googleapis.com/compute/v1/projects/sbtest-150402/global/firewalls/skbankaccount-nodeport].
Creating firewall...done.
NAME                    NETWORK  DIRECTION  PRIORITY  ALLOW      DENY  DISABLED
skbankaccount-nodeport  default  INGRESS    1000      tcp:32713        False
```
Now we will get the successful response on `http://x.x.x.x:32713/sayHello`


## Helm Commands used in the course

|     Helm Command       |     Description          |
| ------------- | ------------- |
| "helm create [NAME]" | Create a default chart with the given name |
| "helm dependencies build" | To recompile the given helm chart |
| "helm install [NAME] [CHART]" | Install the given helm chart into K8s cluster |
| "helm upgrade [NAME] [CHART]" | Upgrades a specified release to a new version of a chart |
| "helm history [NAME]" | Display historical revisions for a given release |
| "helm rollback [NAME] [REVISION]" | Roll back a release to a previous revision |
| "helm uninstall [NAME]" | Uninstall all of the resources associated with a given release |
| "helm template [NAME] [CHART]" | Render chart templates locally along with the values |
| "helm list" | Lists all of the helm releases inside a K8s cluster |