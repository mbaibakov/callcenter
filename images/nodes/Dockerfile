FROM 685321914474.dkr.ecr.eu-west-3.amazonaws.com/orbita/common/corda/node:alpha

COPY entrypoint.sh $CORDA_HOME/entrypoint.sh

CMD ["dockerize", "-wait", "http://networkmap:8090/health", "sh", "entrypoint.sh"]