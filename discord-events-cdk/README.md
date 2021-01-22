# discord-events CDK

AWS infrastructure for `discord-events`.

## Usage

 * `yarn run build`: compile typescript to js and synthesizes the CloudFormation template
 * `yarn run cdk deploy`: deploy this stack to your default AWS account/region
 * `yarn run cdk cdk diff`: compare deployed stack with current state

## Notes

Every deploy will reinstantiate a new image of the application on the instance through ECS.
