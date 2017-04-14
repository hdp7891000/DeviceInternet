//
//  ViewController.m
//  DeviceControl
//
//  Created by yufangmin on 2017/4/6.
//  Copyright © 2017年 yufangmin. All rights reserved.
//

#import "ViewController.h"

@interface ViewController ()
@property (weak, nonatomic) IBOutlet UIImageView *lampImage;
@property (nonatomic) bool lampStatus;
@property (strong, nonatomic) NSTimer *timer;
@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    [self updateLampStatus];
}

-(void)viewWillAppear:(BOOL)animated
{
    _timer = [NSTimer scheduledTimerWithTimeInterval:2 target:self selector:@selector(updateLampStatus) userInfo:nil repeats:YES];
}
-(void)viewWillDisappear:(BOOL)animated
{
    [_timer invalidate];
}

-(void)updateLampStatus
{
    // get remote device's lampStatus
    NSDictionary *propertyListResults = [self jsonDataFromHttpAppAcvite:@"get" key:@"led" value:nil];
    
    NSString *value = [propertyListResults valueForKeyPath:@"value"];
    if ([value isEqualToString:@"off"]) {
        [self updateUI:false];
    }else {
        [self updateUI:true];
    }
}

-(void)setLampStatus:(bool)lampSwitch
{
    // Update the UI
    [self updateUI:lampSwitch];
    
    // Update to remote device
    [self jsonDataFromHttpAppAcvite:@"put" key:@"led" value:lampSwitch ? @"on":@"off"];
}

-(void)updateUI:(bool)lampSwitch
{
    UIImage *image;
    if(lampSwitch) {
        image = [UIImage imageNamed:@"lamp_on"];
    }else{
        image = [UIImage imageNamed:@"lamp_off"];
    }
    [self.lampImage setImage:image];
}

-(NSDictionary*) jsonDataFromHttpAppAcvite:(NSString*)active key:(NSString*)key value:(NSString*)value
{
    NSURL *url;
    if( [active isEqualToString:@"put"] ) {
        url = [NSURL URLWithString:[NSString stringWithFormat:@"http://yfm1202.6655.la:9090/api/a7/control?active=put&key=%@&value=%@",key,value]];
    }else {
        url = [NSURL URLWithString:[NSString stringWithFormat:@"http://yfm1202.6655.la:9090/api/a7/control?active=get&key=%@",key]];
    }
    
    NSData *jsonResults = [NSData dataWithContentsOfURL:url];
    NSDictionary *propertyListResults = [NSJSONSerialization JSONObjectWithData:jsonResults
                                                                        options:0
                                                                          error:NULL];
    return propertyListResults;
}

- (IBAction)lampOn:(id)sender {
    self.lampStatus = true;
}

- (IBAction)lampOff:(id)sender {
    self.lampStatus = false;
}


@end
