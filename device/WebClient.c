#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <fcntl.h>
#include <asm/ioctls.h>

// value 0 is in pc, value 1 is in board. 
#define TEST_IN_DEVICE   0

#define SERVPORT (8080)

#define RECE_BUFFER_SIZE   4096

char *findValueByName(char *source, char *name);
int   streamProcess(char *recebuffer);
void Buzzer(char *s);
void Led(char *s);
int main(int argc, char *argv[])
{
	int i,j, sockfd,recebytes;
	struct hostent *h;
	struct sockaddr_in serv_addr;
    char hostname[] = "127.0.0.1";
    //http://127.0.0.1:8080/api/a7/control?active=get&object=led
	char httprequest[] = "GET http://127.0.0.1/api/a7/control?active=get&object=led HTTP/1.1\r\nHost:127.0.0.1\r\n\r\n";
	char recebuffer[RECE_BUFFER_SIZE];
	printf("Your domain :%s\n",hostname);
	printf("Your httpRequest:\n%s",httprequest);
	if((h=gethostbyname(hostname))==NULL)
	{
		printf("can't get IP\n");
		exit(1);
	}
	printf("HostName  :%s\n",h->h_name);
	serv_addr.sin_addr = *((struct in_addr *)h->h_addr_list[0]);
	const char *ip = inet_ntoa(serv_addr.sin_addr);
	//printf("IP Address:%s   %x\n", ip, serv_addr.sin_addr);
	printf("IP Address:%s   \n", ip);
	if(-1 == (sockfd = socket(AF_INET, SOCK_STREAM,0)))
	{
		printf("socket error\n");
		exit(0);
	}
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_port = htons(SERVPORT);
	serv_addr.sin_addr = *((struct in_addr *)h->h_addr_list[0]);

	i = j = 0;
	while(1)
	{
		i++;
		if(i>1)
			close(sockfd);
			//close(sockfd);
		if(-1 == (sockfd = socket(AF_INET, SOCK_STREAM,0)))
		{
			printf("socket error\n");
			exit(0);
		}
		usleep(500000);
		//printf("Connecting to :%s ", ip);
		if(connect(sockfd, (struct sockaddr *)&serv_addr, sizeof(struct sockaddr)) == -1)
		{
			printf("connect error\n");
			continue;
		}

		if(-1 == write(sockfd,httprequest,strlen(httprequest)))
		{
			printf("send error\n");
			continue;
		}
		//printf("HTTP Request sent\n");
		memset(recebuffer,0,RECE_BUFFER_SIZE);
		if(-1 == (recebytes = read(sockfd, recebuffer, RECE_BUFFER_SIZE)))
		{
			printf("received error\n");
			continue;
		}
		//printf("Received data:\r\n%s\n",recebuffer);

		memset(recebuffer,0,RECE_BUFFER_SIZE);
		if(-1 == (recebytes = read(sockfd, recebuffer, RECE_BUFFER_SIZE)))
		{
			printf("received error\n");
			continue;
		}
		//printf("Received data:\r\n%s\n",recebuffer);
        
		if(-1 ==streamProcess(recebuffer))
			printf("find nothing\n");
		j++;
		//printf("sk:%d  total %d success %d \n",sockfd, i , j);
	}

	return 0;
}

char nled[] = {"value"};
int streamProcess(char *recebuffer)
{
	char *p;
	if(NULL == (p=findValueByName(recebuffer,nled)))
		return -1;
	printf("Led:%s\n",p);
	Led(p);
	return 1;
}

char buffer[100];
char *findValueByName(char *source, char *name)
{
    char *p,*p2;
    int i,j,k;
    memset(buffer,0,100);
    if((p = strstr(source,name)) == NULL)
        return NULL;
    if((p = strchr(p,':')) == NULL)
        return NULL;
    p++;
    if((p2 = strchr(p, '}')) == NULL)
        return NULL;
    j = (int)(p2) - (int)(p);
    memset(buffer,0,100);
    k=0;
    for(i=0;i<j;i++)
    {
        if(*(p+i)=='\"')
            continue;
        if(*(p+i)==',')
            break;
        buffer[k] = *(p+i);
        k++;
    }
    buffer[k] = '\0';
    return buffer;
}

void Led(char *s)
{
	if(strcmp("on",s)) {
        #if TEST_IN_DEVICE
		system("echo 0 > /sys/class/leds/led-err/brightness");
        #endif
    }
	else {
        #if TEST_IN_DEVICE
		system("echo 1 > /sys/class/leds/led-err/brightness");
        #endif
    }
}
